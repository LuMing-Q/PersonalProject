package com.qkj.project.utils;

import com.qkj.project.common.enumerations.StatusCode;
import com.qkj.project.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * @author KeJiang Qi
 * @date 2025/9/8 - 17:54
 * @description 安全解压工具（UTF-8 编码，防 Zip Slip）
 */
@Slf4j
public class ZipUtils {
    private static final byte[] ZIP_MAGIC = {0x50, 0x4B, 0x03, 0x04};

    private static final int ZIP_HEADER_SIZE = 4;

    /**
     * 解压
     * @param zipFile   必须存在且是标准 ZIP
     * @param targetDir 目标目录（会自动创建）
     */
    public static void unzip(Path zipFile, Path targetDir) throws IOException {
        checkZipFile(zipFile);
        Files.createDirectories(targetDir);
        String zipName = com.google.common.io.Files.getNameWithoutExtension(zipFile.getFileName().toString());
        try (ZipArchiveInputStream in =
                     new ZipArchiveInputStream(
                             Files.newInputStream(zipFile), "UTF-8", true, true)) {
            ZipArchiveEntry entry;
            while ((entry = in.getNextZipEntry()) != null) {
                String entryName = entry.getName();
                if (BaseUtil.isEmpty(entryName)) {
                    throw BusinessException.of(StatusCode.CODE_400, "压缩包内存在空文件名，拒绝解压");
                }
                String newEntryName = zipName + "/" + entryName;
                Path resolved = checkZipSlip(targetDir, newEntryName);
                if (entry.isDirectory()) {
                    Files.createDirectories(resolved);
                } else {
                    Files.createDirectories(resolved.getParent());
                    Files.copy(in, resolved, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IllegalArgumentException e) {
            throw BusinessException.of(StatusCode.CODE_400, "压缩包格式或编码错误，请确认是有效的 ZIP 文件且文件名使用 UTF-8 编码" + e);
        }
    }

    /**
     * 魔数校验
     */
    private static void checkZipFile(Path zip) throws IOException {
        if (Files.notExists(zip) || !Files.isRegularFile(zip)) {
            throw BusinessException.of(StatusCode.CODE_400, "ZIP 文件不存在或不是普通文件：" + zip);
        }
        byte[] header = Files.readAllBytes(zip);
        if (header.length < ZIP_HEADER_SIZE ||
                header[0] != ZIP_MAGIC[0] ||
                header[1] != ZIP_MAGIC[1] ||
                header[2] != ZIP_MAGIC[2] ||
                header[3] != ZIP_MAGIC[3]) {
            throw BusinessException.of(StatusCode.CODE_400, "ZIP 文件头错误，可能不是有效压缩包");
        }
    }

    /**
     * 防路径遍历
     */
    public static Path checkZipSlip(Path targetDir, String entryName) {
        Path normalized = targetDir.resolve(entryName).normalize();
        if (!normalized.startsWith(targetDir.normalize())) {
            throw BusinessException.of(StatusCode.CODE_400, "压缩包内存在非法路径（Zip Slip）：" + entryName);
        }
        return normalized;
    }

    /**
     * 删除文件
     * @param p 文件路径
     */
    public static void accept(Path p) {
        try {
            Files.deleteIfExists(p);
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 递归删除目录
     * @param path 路径
     */
    public static void deleteDirectory(Path path) {
        if (Files.notExists(path)) {
            return;
        }
        try (Stream<Path> stream = Files.walk(path)) {
            stream.sorted(Comparator.reverseOrder())
                    .forEach(ZipUtils::accept);
        } catch (IOException e) {
            log.error("文件删除失败: {}", e.getMessage());
            throw BusinessException.of(StatusCode.CODE_400, e.getMessage());
        }
    }
}

