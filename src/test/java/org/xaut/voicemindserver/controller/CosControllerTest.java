package org.xaut.voicemindserver.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CosControllerTest {

    @Autowired
    private CosController cosController;

    @Test
    public void testGetCosSts() throws Exception {
        Map<String, Object> result = cosController.getCosSts();

        assertNotNull(result);
        assertTrue(result.containsKey("tmpSecretId"));
        assertTrue(result.containsKey("tmpSecretKey"));
        assertTrue(result.containsKey("sessionToken"));
        assertTrue(result.containsKey("expiredTime"));
        assertTrue(result.containsKey("bucket"));
        assertTrue(result.containsKey("region"));
        assertTrue(result.containsKey("prefix"));

        System.out.println("STS临时密钥返回：");
        result.forEach((k, v) -> System.out.println(k + " : " + v));
    }
}
