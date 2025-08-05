package org.xaut.voicemindserver.Service;

import com.qcloud.cos.COSClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ObjectStorageServiceTest {

    @Autowired
    private ObjectStorageService objectStorageService;

    @Test
    void testUploadToCOS() throws Exception {
        // 准备一个本地测试音频文件
        String filePath = "src/main/resources/data/Q1.wav";
        FileInputStream fis = new FileInputStream(filePath);

        MockMultipartFile multipartFile = new MockMultipartFile(
                "audio",
                "Q1.wav",
                "audio/mpeg",
                fis
        );

        // 调用上传方法
        String url = objectStorageService.upload(multipartFile, "test_user", "test_question");

        System.out.println("上传成功，文件 URL：" + url);

        assertNotNull(url);
        assertTrue(url.contains(".myqcloud.com"));
    }
}
