package org.xaut.voicemindserver.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;               // 主键 ID（可选）
    private String username;      // 学号
    private String password;       // 密码（建议加密存储）
    private LocalDateTime registerTime;  // 注册时间
    private String role; // 用户角色
}
