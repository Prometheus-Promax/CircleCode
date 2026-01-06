package org.bteam.circlecode.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.bteam.circlecode.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    User selectByUsername(String username);

    User selectByEmail(String email);

    User selectByPhone(String phone);

    int insertUser(User user);

    int deleteByUsername(String username);
}
