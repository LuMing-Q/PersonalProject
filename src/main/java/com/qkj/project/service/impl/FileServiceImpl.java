package com.qkj.project.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qkj.project.common.enumerations.StatusCode;
import com.qkj.project.common.exception.BusinessException;
import com.qkj.project.config.MinioCfg;
import com.qkj.project.dao.UploadedFileDao;
import com.qkj.project.entity.FileUpload;
import com.qkj.project.entity.UploadedFile;
import com.qkj.project.entity.dto.ChunkDTO;
import com.qkj.project.entity.dto.FileNodeDTO;
import com.qkj.project.service.FileService;
import com.qkj.project.utils.BaseUtil;
import com.qkj.project.utils.ZipUtils;
import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author KeJiang Qi
 * @date 2024/11/4 - 16:17
 * @description 文件上传实现类
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Resource(name = "minio")
    private MinioClient minioClient;

    @Resource
    private MinioCfg minioCfg;

    @Resource
    private UploadedFileDao uploadedFileDao;

    private static final String TEMP_DIR = System.getProperty("user.dir") + "/uploadChunks/";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String complexFileUpload(Map<String, Object> request) {
        request.forEach((key, value) -> {
            if (value instanceof MultipartFile) {
                MultipartFile file = (MultipartFile) value;
                // 文件上传操作
                String fileName = file.getOriginalFilename();
                System.out.println(fileName);
            }
        });
        //文件以及数据处理操作
        return "上传成功";
    }

    @Override
    public FileUpload upload(MultipartFile file, String fileUrl) {
        BaseUtil.folderPathValidator(fileUrl);
        BaseUtil.fileNameValidator(file.getOriginalFilename());
        String objectName;
        if (BaseUtil.isEmpty(fileUrl)) {
            objectName = file.getOriginalFilename();
        } else {
            objectName = fileUrl + "/" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "/" +
                    file.getOriginalFilename();
        }
        long filesize = file.getSize();
        String filetype = file.getContentType();
        FileUpload fileUpload = new FileUpload()
                .setId(BaseUtil.uuid())
                .setFileName(file.getOriginalFilename())
                .setObjectName(objectName)
                .setFileSize(filesize)
                .setFileType(filetype);
        String path = uploadFile(file, objectName);
        log.info("文件已上传到 MinIO: {}", path);
        return fileUpload;
    }

    @Override
    public void updateFolder(MultipartFile file) throws Exception {
        // 把 zip 存到临时文件
        String originalName = BaseUtil.fileNameValidator(file.getOriginalFilename());
        if (BaseUtil.isEmpty(originalName)) {
            throw BusinessException.of(StatusCode.CODE_400, "上传文件缺少文件名");
        }
        // 放在系统临时目录下，保持原名
        Path tempZip = Paths.get(System.getProperty("java.io.tmpdir"), originalName);
        file.transferTo(tempZip.toFile());
        // 解压到临时目录
        Path tempDir = Files.createTempDirectory("unzipped-");
        ZipUtils.unzip(tempZip, tempDir);
        // 遍历解压后的文件夹并上传
        try {
            List<Path> fileList;
            try (Stream<Path> stream = Files.walk(tempDir)) {
                fileList = stream.filter(Files::isRegularFile)
                        .collect(Collectors.toList());
            }
            for (Path path : fileList) {
                String objectName = tempDir.relativize(path)
                        .toString()
                        .replace("\\", "/");
                minioClient.uploadObject(
                        UploadObjectArgs.builder()
                                .bucket(minioCfg.getZipBucketName())
                                .object(objectName)
                                .filename(path.toString())
                                .build());
            }
        } finally {
            ZipUtils.deleteDirectory(tempDir);
            Files.deleteIfExists(tempZip);
        }
    }

    @Override
    public List<FileNodeDTO> getAllUploadedFileNodes() {
        List<UploadedFile> files = uploadedFileDao.findAll();
        List<FileNodeDTO> nodes = new ArrayList<>();
        for (UploadedFile f : files) {
            FileNodeDTO dto = new FileNodeDTO();
            dto.setId(f.getId());
            dto.setFileHash(f.getFileHash());
            dto.setFileName(f.getFileName());
            dto.setPath(f.getPath());
            dto.setSize(f.getSize());
            try {
                dto.setUploadedChunks(f.getUploadedChunks() == null ? new ArrayList<>() :
                        OBJECT_MAPPER.readValue(f.getUploadedChunks(), new TypeReference<List<Integer>>() {
                        }));
            } catch (JsonProcessingException e) {
                throw BusinessException.of(StatusCode.CODE_400, "解析已上传分片失败: " + e.getMessage());
            }
            dto.setStatus(f.getStatus());
            dto.setFolder(f.getFolder());
            dto.setParentPath(f.getParentPath());
            nodes.add(dto);
        }
        return nodes;
    }

    @Override
    public void saveOrUpdateFileNodes(List<FileNodeDTO> nodes) {
        for (FileNodeDTO node : nodes) {
            UploadedFile file = uploadedFileDao.findByPathAndHash(node.getPath(), node.getFileHash());
            if (file == null) {
                file = new UploadedFile();
                file.setId(BaseUtil.uuid());
                file.setCreateTime(LocalDateTime.now());
            }
            if ("done".equals(file.getStatus())) {
                continue;
            }
            if (node.getFolder()) {
                file.setFileHash(node.getPath());
            } else if (node.getFileHash() != null) {
                file.setFileHash(node.getFileHash());
            }
            file.setFileName(node.getFileName());
            file.setPath(node.getPath());
            file.setSize(node.getSize());
            try {
                file.setUploadedChunks(OBJECT_MAPPER.writeValueAsString(node.getUploadedChunks()));
            } catch (JsonProcessingException e) {
                throw BusinessException.of(StatusCode.CODE_400, "保存已上传分片失败: " + e.getMessage());
            }
            file.setStatus(node.getStatus());
            file.setFolder(node.getFolder());
            file.setParentPath(node.getParentPath());
            file.setUpdateTime(LocalDateTime.now());
            uploadedFileDao.save(file);
        }
    }

    @Override
    public FileUpload mergeChunks(ChunkDTO chunkDTO) {
        File tempDir = new File(TEMP_DIR, chunkDTO.getFileHash());
        BaseUtil.fileNameValidator(chunkDTO.getFileName());
        File mergedFile = mergeChunkFiles(tempDir,chunkDTO.getFileName(), chunkDTO.getTotalChunks());
        CompletableFuture.runAsync(() -> {
            try (FileInputStream fis = new FileInputStream(mergedFile)) {
                MockMultipartFile file = new MockMultipartFile(chunkDTO.getFileName(), chunkDTO.getFileName(), "application/octet-stream", fis);
                String objectName = BaseUtil.isEmpty(chunkDTO.getFileUrl()) ?
                        file.getOriginalFilename() :
                        chunkDTO.getFileUrl() + "/" + file.getOriginalFilename();
                String path = uploadFile(file, objectName);
                log.info("合并文件已上传到 MinIO: {}", path);
            } catch (Exception e) {
                log.error("合并文件上传失败", e);
            } finally {
                deleteTempDirAsync(tempDir);
            }
        });
        return new FileUpload().
                setId(BaseUtil.uuid()).
                setFileName(chunkDTO.getFileName()).
                setObjectName(chunkDTO.getFileUrl() + "/" + chunkDTO.getFileName()).
                setFileSize(mergedFile.length()).
                setFileType("application/octet-stream");
    }

    @Override
    public List<Integer> checkChunks(String fileHash) {
        UploadedFile file = uploadedFileDao.findByFileHash(fileHash);
        if (file == null) {
            return Collections.emptyList();
        }
        try {
            return OBJECT_MAPPER.readValue(file.getUploadedChunks(), new TypeReference<List<Integer>>() {
            });
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public FileUpload uploadChunk(MultipartFile file, String fileUrl, String fileHash, Integer chunkIndex) {
        BaseUtil.fileNameValidator(file.getOriginalFilename());
        BaseUtil.folderPathValidator(fileUrl);
        String dirPath = TEMP_DIR + fileHash;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw BusinessException.of(StatusCode.CODE_500, "临时目录创建失败: " + dirPath);
            }
        }
        File chunkFile = new File(dir, String.valueOf(chunkIndex));
        try (InputStream in = file.getInputStream();
             FileOutputStream out = new FileOutputStream(chunkFile)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            throw new RuntimeException("分片写入失败", e);
        }
        return new FileUpload().
                setFileName(file.getOriginalFilename()).
                setObjectName(fileUrl + "/" + file.getOriginalFilename()).
                setFileSize(file.getSize()).
                setFileType(file.getContentType());
    }

    /**
     * 合并文件
     * @param tempDir 文件路径
     * @param fileName 文件名称
     * @param totalChunks 分片总数
     * @return 合并后的文件
     */
    private File mergeChunkFiles(File tempDir, String fileName, int totalChunks) {
        File[] chunks = tempDir.listFiles((dir, name) -> name.matches("\\d+"));
        if (chunks == null || chunks.length != totalChunks) {
            deleteTempDirAsync(tempDir);
            throw BusinessException.of(StatusCode.CODE_400, "分片缺失，无法合并");
        }
        Arrays.sort(chunks, Comparator.comparingInt(f -> Integer.parseInt(f.getName())));
        File mergedFile = new File(tempDir, "merged_" + fileName);
        try (FileOutputStream out = new FileOutputStream(mergedFile)) {
            byte[] buffer = new byte[1024 * 1024 * 8];
            for (File chunk : chunks) {
                try (FileInputStream in = new FileInputStream(chunk)) {
                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                }
            }
        } catch (IOException e) {
            throw BusinessException.of(StatusCode.CODE_500, fileName + "分片合并失败: " + e.getMessage());
        }
        return mergedFile;
    }

    @Async
    @Override
    public void deleteTempDirAsync(File dir) {
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (!f.delete()) {
                        log.warn("临时分片文件删除失败: {}", f.getAbsolutePath());
                    }
                }
            }
            if (!dir.delete()) {
                log.warn("临时分片目录删除失败: {}", dir.getAbsolutePath());
            }
        }
    }

    @Override
    public String uploadFile(MultipartFile file, String objectName) {
        try (InputStream inputStream = file.getInputStream()) {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioCfg.getBucketName()).build());
            if (!exists) {
                throw BusinessException.of(StatusCode.CODE_400, "桶" + minioCfg.getBucketName() + "不存在");
            }
            ObjectWriteResponse response = minioClient.putObject(PutObjectArgs.builder().
                    bucket(minioCfg.getBucketName()).
                    object(objectName).
                    contentType(file.getContentType()).
                    stream(inputStream, file.getSize(), -1).
                    build());
            return response.etag();
        } catch (Exception e) {
            log.error("❌ 文件上传失败: {}", objectName, e);
            throw BusinessException.of(StatusCode.CODE_400, "文件上传失败: " + e.getMessage());
        }
    }
}
