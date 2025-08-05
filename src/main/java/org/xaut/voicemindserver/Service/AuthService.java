package org.xaut.voicemindserver.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.xaut.voicemindserver.Mapper.UserMapper;
import org.xaut.voicemindserver.entity.User;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private UserMapper userMapper;
    private PasswordEncoder passwordEncoder;

    AuthService(UserMapper userMapper, PasswordEncoder passwordEncoder){
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean register(String username, String password, String role) {
        if (userMapper.findByUsername(username) != null) return false;

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ROLE_USER"); // 默认角色
        user.setRegisterTime(LocalDateTime.now());

        userMapper.insert(user);
        return true;
    }
}
