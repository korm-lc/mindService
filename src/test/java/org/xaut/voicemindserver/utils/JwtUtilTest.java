package org.xaut.voicemindserver.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;
@Slf4j
@SpringBootTest
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void testGenerateToken() {
        String userId = "20250802123";
        String token = jwtUtil.generateToken(userId);
        assertNotNull(token);
        System.out.println("生成的Token: " + token);
    }

    @Test
    public void testParseToken() {
        String userId = "student123";
        String token = jwtUtil.generateToken(userId);
        String subject = jwtUtil.getSubjectFromToken(token);

        assertEquals(userId, subject);
    }

    @Test
    public void testValidateToken() {
        String token = jwtUtil.generateToken("user456");
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    public void testInvalidToken() {
        String fakeToken = "abc.def.ghi";
        assertFalse(jwtUtil.validateToken(fakeToken));
    }
}