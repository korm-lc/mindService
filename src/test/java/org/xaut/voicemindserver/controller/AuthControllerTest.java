package org.xaut.voicemindserver.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional    // 每个测试结束后回滚，数据库不留脏数据
@Rollback
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testRegisterSuccess() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .param("username", "testuser")
                        .param("password", "123456")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("注册成功"));
    }

    @Test
    public void testRegisterConflict() throws Exception {
        // 先注册一次
        mockMvc.perform(post("/auth/register")
                        .param("username", "testuser2")
                        .param("password", "123456")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk());

        // 再注册同名用户，应该返回409冲突
        mockMvc.perform(post("/auth/register")
                        .param("username", "testuser2")
                        .param("password", "123456")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isConflict())
                .andExpect(content().string("用户已存在"));
    }
}
