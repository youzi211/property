package org.yzc.property1.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.yzc.property1.dao.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 可以添加自定义查询方法
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username); // 根据用户名查找用户
    //用户名密码登录
    @Select("SELECT * FROM user WHERE username = #{username} AND password = #{password}")
    User findByUsernameAndPassword(String username, String password);

}