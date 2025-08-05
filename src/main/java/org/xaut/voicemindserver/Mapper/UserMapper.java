package org.xaut.voicemindserver.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.xaut.voicemindserver.entity.User;

@Mapper
public interface UserMapper {
    User findByUsername(String username);

    void insert(User user);

    void deleteByUsername(String username);
}
