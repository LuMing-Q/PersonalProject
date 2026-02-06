package com.qkj.project;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author KeJiang Qi
 * @date 2025/11/21 - 16:12
 * @description Axios 风格 RestTemplate 封装
 */
public class AxiosClient {

    private final RestTemplate rest;
    private final String baseUrl;

    private static final String TOKEN =
            "Bearer eyJraWQiOiJlYTk4ZWZmY2NiZDczZTA0ODliYTFkY2ZmMzgxYWZiMyIsImFsZyI6IlJTMjU2In0.eyJqdGkiOiI5MGZhZjY2MzcyYmY0NWViODdlZDVjZDA2MmRkZDkzMSIsInN1YiI6Im1hbmFnZXIiLCJyb2xlcyI6WyI3NGNmYjk2N2M2N2YyMGM1NGQ3YWNmOWRkMmJmNTA4YiIsIjcyNThjNGNkMGM5MjgxMTNiZjMyNmU3MTY4OGM2YTE0IiwiMTZjYzI1ZDQxMDg0ZTJlNzhlZmJmZWFkNWM1NjU0YWYiLCIxIiwiNTNiNTQ4NmE4NTNkMmYzYjEwNWFmY2I4OTYxYjNjOGQiLCI3MDk2N2E3OTExYTMwYmIzYzc4MDE1MDU1ZDA1ZjBhZSIsIjNmNTllZTY3NzZhOTA2YjNlZGUyMGVmNTI1MGFhMjIyIiwiMzU4Mzc1ZjY0MDE1ZDhhYzBjZmJkNTZmMDI5YTg0OTAiLCI0NmVmZGExN2NlYjUxOWQxNWFjNDQ0N2Y1Nzg1ZGYwZSIsIjc5ZWY3YTBmNzEwZGMzZGI2YTcxMWY4NWYxYmFiMzU5IiwiZTQyOTkxNGI5MGM0MzgyNzIyYWY0MWQ3MWM3ZWRhNzMiLCI2OTQyN2FlYTRmOTM3ODI0N2VhNWQzOWEzZjJhZjE1NCIsIjkyZjNkYzA4NzA1MDcwZjE3NTk3MTY4MDNlZjI3YmE0IiwiMGU4OTc1NzE4YTBlNjBiNGRiOTRmYzBjYzYwZTk3YjAiLCI2IiwiZGI1NGRlOGVkOTgyMjdjYjA5ZWM2Mjc2OTNhYjJmZjIiLCI1IiwiN2ZhOGEzNWVhY2RhYzM2NjE3ZTAzMTE5YjEzZTBkZjMiLCIzNzM1ZjllMmZkMWY4MWM2ZTgxNDFmNWZiYTBmNmFiNiIsIjYxOGQwM2FjODc3MjdlZTRhNzNlNjRjOTkwZTY1N2ViIiwiNCIsIjgxYmE4Y2QzNzIxMzJlYmJhZmFmOTFkZGFkZTIxNmNjIiwiM2VlYWFjODcxMzdiNjQxZDQ0MGY5NDhjZWQxOGQwNDciLCIzIiwiMzM3OWU3YjkxMDAxYTA2YThmZGMwMDRkZDc5YjU3NTAiLCJkNTU1ZmY1NGIwYzMxMmFlNjk5MmM2ZTA2N2Y5MDZhMyIsImRhYzBjOTg0ODU3NzI2NmU1OWU4NjA2YmI5NTg0NzViIiwiZjdlOTE5ODEwOWU3YTYzNjU0ZjgzYzQ3YzA4NzNlMTkiLCI2ZDAwMzE4ZjBhOTBhZDNhYjY3NTYwYWZhNGFmZjM2YyIsIjQ2YjQ1NWU4ZDZhZDRkNTc3YjZjNDA5MjQ1ZDEyMzNhIiwiMiIsIjk5YzJmNDM3NDUxZTkyYTM5MTNlMjY4NmFhNDI2OTJkIl0sImdyb3VwcyI6W10sImV4dGVuZEluZm8iOnt9LCJ1c2VySWQiOiIxIiwic2lkIjoiMjcyYTY3ZmQtODljMS00NjFmLWJmZmEtM2IzZjQ3MzAxYTg3IiwidWlkIjoiMSIsImdyYW50X3R5cGUiOiJhdXRob3JpemF0aW9uX2NvZGUiLCJwaG9uZSI6IiIsInNjb3BlIjpbInByaXZhY3kiLCJwaG9uZSIsIm9wZW5pZCIsImVtYWlsIiwicHJvZmlsZSJdLCJhcHBJZCI6ImVhOThlZmZjY2JkNzNlMDQ4OWJhMWRjZmYzODFhZmIzIiwibmFtZSI6Im1hbmFnZXIiLCJkZXBhcnRtZW50cyI6W10sImVtYWlsIjoiIiwidXNlcm5hbWUiOiJtYW5hZ2VyIiwiaXNzIjoicmJhYyIsImlhdCI6MTc2MzY5NzU0NywiZXhwIjoxNzYzNzMzNTQ3LCJhdWQiOiJlYTk4ZWZmY2NiZDczZTA0ODliYTFkY2ZmMzgxYWZiMyJ9.OccSQtkC8yQAHgEt00xzAB2evaIugs1O48_F9rVhFx_wjHXZMkQhRykoMDkzx2Z52vBHlrSqXKqFYqg-1VW9gA";

    public AxiosClient(String baseUrl) {
        this.baseUrl = baseUrl;
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        // 10分钟
        factory.setConnectTimeout(600_000);
        factory.setReadTimeout(600_000);
        this.rest = new RestTemplate(factory);
    }

    /** GET (带 token + 返回 data 字段) */
    public <T> T get(String url, Map<String, Object> query, Class<T> clazz) {
        String fullUrl = buildUrl(url, query);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", TOKEN);

        HttpEntity<Void> req = new HttpEntity<>(headers);

        ResponseEntity<T> res = rest.exchange(
                fullUrl,
                HttpMethod.GET,
                req,
                clazz
        );

        return res.getBody();
    }

    public <T> T get(String url, Map<String, Object> query, ParameterizedTypeReference<T> typeRef) {
        String fullUrl = buildUrl(url, query);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", TOKEN);

        HttpEntity<Void> req = new HttpEntity<>(headers);

        ResponseEntity<T> res = rest.exchange(
                fullUrl,
                HttpMethod.GET,
                req,
                typeRef
        );

        return res.getBody();
    }


    /** POST（可带 body） */
    public <T> T post(String url, Object body, Map<String, Object> query, Class<T> clazz) {
        String fullUrl = buildUrl(url, query);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", TOKEN);

        HttpEntity<Object> req = new HttpEntity<>(body, headers);

        ResponseEntity<Map> res = rest.exchange(fullUrl, HttpMethod.POST, req, Map.class);

        return (T) res.getBody();
    }

    /** POST 二进制流（上传分片） */
    public void postStream(String url, byte[] bytes, Map<String, Object> query) {
        String fullUrl = buildUrl(url, query);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.add("Authorization", TOKEN);

        HttpEntity<byte[]> req = new HttpEntity<>(bytes, headers);

        rest.exchange(fullUrl, HttpMethod.POST, req, String.class);
    }

    /** URL 构建器 */
    private String buildUrl(String url, Map<String, Object> query) {
        StringBuilder sb = new StringBuilder(baseUrl).append(url);
        if (query != null && !query.isEmpty()) {
            sb.append("?");
            query.forEach((k, v) -> sb.append(k)
                    .append("=")
                    .append(v.toString())
                    .append("&"));
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
