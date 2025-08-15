package org.xaut.voicemindserver.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.xaut.voicemindserver.utils.JwtUtil;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
public class CosControllerTest {

    @Autowired
    private CosController cosController;
    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void testGetCosSts() throws Exception {
        String token = jwtUtil.generateToken("testUserId");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        ResponseEntity<?> response = cosController.getCosSts(request.getHeader("Authorization"));
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> result = (Map<String, Object>) response.getBody();

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
