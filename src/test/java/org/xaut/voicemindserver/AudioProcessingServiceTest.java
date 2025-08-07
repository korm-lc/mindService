package org.xaut.voicemindserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.xaut.voicemindserver.Service.AudioService;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AudioProcessingServiceTest {

    @Autowired
    private AudioService audioService;

    @Test
    public void testUploadAudio() throws IOException {
        String userId = "test_user";
        String questionId = "1";

        // 本地准备一个测试音频文件路径
        String testAudioPath = "src/main/resources/data/Q1.wav";
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "Q1.wav",
                "audio/wav",
                new FileInputStream(testAudioPath)
        );

        String savedPath = audioService.handleUpload(mockFile, userId, questionId).toString();
        System.out.println("文件保存路径：" + savedPath);

        // 验证音频文件是否存在
        Path audioPath = Path.of(savedPath);
        assertTrue(Files.exists(audioPath), "音频文件应已保存");

        // 验证文本文件是否生成
        Path textPath = audioPath.resolveSibling("Question" + questionId + ".txt");
        assertTrue(Files.exists(textPath), "应生成对应的文本文件");

        // 验证文本内容不为空
        String content = Files.readString(textPath);
        assertFalse(content.trim().isEmpty(), "文本内容不应为空");

        System.out.println("识别内容：" + content);
    }
}
