package org.xaut.voicemindserver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import org.xaut.voicemindserver.Mapper.AudioMapper;
import org.xaut.voicemindserver.Service.AudioService;
import org.xaut.voicemindserver.Service.FastApiService;
import org.xaut.voicemindserver.Service.ObjectStorageService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AudioServiceTest {

    @InjectMocks
    private AudioService audioService;

    @Mock
    private ObjectStorageService objectStorageService;

    @Mock
    private FastApiService fastApiService;

    @Mock
    private AudioMapper audioMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleUpload_Success() throws IOException {
        // 模拟参数
        String userId = "user123";
        String questionId = "q456";
        String audioUrl = "https://fake.cdn.com/audio/123.mp3";
        String fastApiResult = "{\"text\": \"你好世界\", \"features\": [1.0, 2.0]}";

        MockMultipartFile mockFile = new MockMultipartFile(
                "audio", "test.wav", "audio/wav", "dummy".getBytes()
        );

        // Mock 返回值
        when(objectStorageService.upload(mockFile, userId, questionId)).thenReturn(audioUrl);
        when(fastApiService.transcribe(audioUrl, userId, questionId)).thenReturn(fastApiResult);

        // 调用方法
        Map<String, Object> result = audioService.handleUpload(mockFile, userId, questionId);

        // 验证
        assertEquals(audioUrl, result.get("audio_url"));
        assertEquals(fastApiResult, result.get("fastapi_result"));

        // 验证 mapper 方法被调用
        verify(audioMapper, times(1)).saveAudioUrl(eq(userId), eq(questionId), eq(audioUrl), any(LocalDateTime.class));
    }
}
