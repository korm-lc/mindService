package org.xaut.voicemindserver.Service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.xaut.voicemindserver.Mapper.UserMapper;
import org.xaut.voicemindserver.entity.User;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    // 这里注入你的用户仓库，比如MyBatis或者JPA的UserMapper
    private UserMapper userMapper;

    public CustomUserDetailsService(UserMapper userMapper){
        this.userMapper = userMapper;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        List<SimpleGrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),  // 用 studentId 作为用户名
                user.getPassword(),
                authorities
        );
    }

}

