package org.xaut.voicemindserver.Service;

import io.jsonwebtoken.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AudioUploadIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testRealAudioUpload() throws IOException {
        // 真实文件，放到resources目录下，或者你用绝对路径
        File audioFile = new File("src/main/resources/data/Q1.wav");
        assertTrue(audioFile.exists(), "测试音频文件不存在");

        // MultipartBody准备
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("audio", new FileSystemResource(audioFile));
        parts.add("user_id", "realUser");
        parts.add("question_id", "realQuestion");

        // 发送请求
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/upload_audio",
                new HttpEntity<>(parts, createMultipartHeaders()),
                Map.class);

        // 检查响应
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().get("audio_url"));
        assertNotNull(response.getBody().get("fastapi_result"));

        System.out.println("上传成功，返回: " + response.getBody());
    }

    private HttpHeaders createMultipartHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return headers;
    }
}
