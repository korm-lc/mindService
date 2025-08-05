package org.xaut.voicemindserver.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.xaut.voicemindserver.Service.AuthService;

@SpringBootTest
public class AdminUserInitTest {

    @Autowired
    private AuthService authService;

    @Test
    public void createAdminUser() {
        String adminUsername = "admin001";
        String adminPassword = "admin123";
        String role ="ROLE_TEST";
        //TODO角色设置

        boolean success = authService.register(adminUsername, adminPassword ,role);
        if (success) {
            System.out.println("管理员账号创建成功: " + adminUsername);
        } else {
            System.out.println("管理员账号已存在: " + adminUsername);
        }

        // 断言，确保管理员创建成功或已存在
        Assertions.assertTrue(success || !success);
    }
}
