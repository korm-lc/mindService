package org.xaut.voicemindserver.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
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

        log.debug("STS临时密钥返回：");
        result.forEach((k, v) -> System.out.println(k + " : " + v));
    }
}
