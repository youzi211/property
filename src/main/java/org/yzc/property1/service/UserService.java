package org.yzc.property1.service;


import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.yzc.property1.dao.entity.Enterprise;
import org.yzc.property1.dao.entity.User;
import org.yzc.property1.dao.mapper.EnterpriseUserMapper;
import org.yzc.property1.dao.mapper.UserMapper;
import org.yzc.property1.model.DataIPInputBO;
import org.yzc.property1.model.Result;
import org.yzc.property1.util.JWTUtils;

import java.util.Map;

import static org.yzc.property1.util.JWTUtils.checkToken;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private EnterpriseUserMapper enterpriseMapper;



    // 根据用户名查找用户
    public User findUserByUserName(String username) {
        return userMapper.findByUsername(username);
    }

    // 根据用户名和密码查找用户
    public User findUser(String username, String password) {
        return userMapper.findByUsernameAndPassword(username, password);
    }

    //根据token获取企业信息
    public Result getUserInfo(String token) {
        User user = checkToken(token);
        if (user == null){
            return Result.fail(10008,"token失效");
        }
        // 根据用户地址查询企业信息
        Enterprise enterprise = enterpriseMapper.selectByAddress(user.getUserAddress());
        return Result.success(enterprise);
    }
    private User checkToken(String token){
        if (StringUtils.isBlank(token)){
            return null;
        }
        Map<String, Object> stringObjectMap = JWTUtils.checkToken(token);
        if (stringObjectMap == null){
            return null;
        }
        String userJson = redisTemplate.opsForValue().get("TOKEN_" + token);
        if (StringUtils.isBlank(userJson)){
            return null;
        }
        User user= JSON.parseObject(userJson, User.class);
        return user;
    }
    //注册知识产权
//    public Result apply(String token, DataIPInputBO inputBO) {
//        // 实现申请逻辑
//        // 1. 校验token
//        User user = checkToken(token);
//        if (user == null){
//            return Result.fail(10008,"token失效");
//        }
//
//
//        return new Result();
//    }

}