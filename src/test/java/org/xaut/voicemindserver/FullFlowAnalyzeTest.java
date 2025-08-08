package org.xaut.voicemindserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class FullFlowAnalyzeTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testFullAnalyzeFlow() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // 1. 登录，获取token
        String loginJson = "{\"username\":\"admin001\", \"password\":\"admin123\"}";

        String loginResponse = mockMvc.perform(
                        MockMvcRequestBuilders.post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginJson)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(loginResponse).get("token").asText();

        // 2. 上传音频文件
        byte[] audioBytes = Files.readAllBytes(Paths.get("src/main/resources/data/Q5.wav"));
        MockMultipartFile audioFile = new MockMultipartFile(
                "audio",            // form-data 中的字段名
                "test.wav",         // 上传的文件名
                "audio/wav",        // 文件 MIME 类型
                audioBytes          // 文件字节内容
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/upload_audio")
                        .file(audioFile)
                        .param("userId", "testuser")
                        .param("question_id", "13")
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionId").exists())
                .andExpect(jsonPath("$.fileUrl").exists())
                .andExpect(jsonPath("$.userId").exists());


        // 3. 调用分析接口 /api/audio/analyze
        mockMvc.perform(MockMvcRequestBuilders.post("/api/audio/analyze")
                        .param("questionId", "13")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.probability").exists())
                .andExpect(jsonPath("$.text").exists())
                .andExpect(jsonPath("$.features").isArray());
    }
}
