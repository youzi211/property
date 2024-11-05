    package org.yzc.property1.service;

    import com.alibaba.fastjson.JSON;
    import com.alibaba.fastjson.JSONObject;
    import org.apache.commons.codec.digest.DigestUtils;
    import org.apache.commons.lang3.StringUtils;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.redis.core.RedisTemplate;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import org.yzc.property1.dao.entity.Enterprise;
    import org.yzc.property1.dao.entity.User;
    import org.yzc.property1.dao.mapper.EnterpriseUserMapper;
    import org.yzc.property1.dao.mapper.UserMapper;
    import org.yzc.property1.model.EnterpriseUser;
    import org.yzc.property1.model.ErrorCode;
    import org.yzc.property1.model.Result;
    import org.yzc.property1.model.UserDTO;
    import org.yzc.property1.model.params.LoginParam;
    import org.yzc.property1.util.HttpUtils;
    import org.yzc.property1.util.JWTUtils;

    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.List;
    import java.util.Map;
    import java.util.concurrent.TimeUnit;

    /**
     * @author 86176
     */
    @Service
    @Transactional
    public class LoginService  {
        @Autowired
        private UserService userService;
        @Autowired
        private RedisTemplate<String,String> redisTemplate;
        @Autowired
        private UserMapper userMapper;
        @Autowired
        private EnterpriseUserMapper enterpriseUserMapper;

        //加密盐
        private static final String slat = "yzc!@#";

        public Result login(LoginParam loginParam) {
            /**
             * 检查参数是否合法
             * 根据用户名和密码去user表中查询 是否存在
             * 如果不存在 登录失败
             * 如果存在，使用JWT 生成token 返回前端
             * token放入redis当中 token：user信息，设置过期时间
             * （登录认证的时候 先认证token字符串是否合法，去redis认证是否存在）
             */
            String username = loginParam.getUsername();
            String password = loginParam.getPassword();
            if (StringUtils.isBlank(username)||StringUtils.isBlank(password)){
                return Result.fail(ErrorCode.PARAMS_ERROR.getCode(),ErrorCode.PARAMS_ERROR.getMsg());
            }
            password= DigestUtils.md5Hex(password+slat);
            User user=userService.findUser(username,password);
            if (user==null){
                return Result.fail(ErrorCode.ACCOUNT_PWD_NOT_EXIST.getCode(),ErrorCode.ACCOUNT_PWD_NOT_EXIST.getMsg());
            }
            String token = JWTUtils.createToken(user.getId());
            redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(user),1, TimeUnit.DAYS);
            return Result.success(user.getRoles(),token);
        }


      public Result register(EnterpriseUser enterpriseUser) {
    // 检查用户参数是否合法
    String userName = enterpriseUser.getUsername();
    String password = enterpriseUser.getPassword();
    if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
        return Result.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
    }

    // 根据用户名获取用户地址
    String address = HttpUtils.commonReq(userName);
    if (StringUtils.isBlank(address)) {
        return Result.fail(ErrorCode.ADDRESS_NOT_FOUND.getCode(), "用户地址未找到");
    }

    // 检查用户名是否已存在
    if (userService.findUserByUserName(userName) != null) {
        return Result.fail(ErrorCode.ACCOUNT_EXIST.getCode(), ErrorCode.ACCOUNT_EXIST.getMsg());
    }

    // 创建新的 User 对象并设置属性
    User newUser = new User();
    newUser.setUserAddress(address)
            .setUsername(userName)
            .setPassword(DigestUtils.md5Hex(password + slat))
            .setRoles("user");

    // 将新的用户插入到数据库
    try {
        userMapper.insert(newUser);
    } catch (Exception e) {
        return Result.fail(ErrorCode.DB_ERROR.getCode(), "数据库插入失败");
    }

    // 创建新的 Enterprise 对象
    Enterprise enterprise = new Enterprise()
            .setUserAddress(address)
            .setEnterpriseName(enterpriseUser.getEnterpriseName())
            .setFinancingScale(enterpriseUser.getFinancingScale())
            .setIsRegistered(false)
            .setForeignInvestmentRatio(enterpriseUser.getForeignInvestmentRatio())
            .setDomesticOrForeign(enterpriseUser.getDomesticOrForeign())
            .setRegistrationLocation(enterpriseUser.getRegistrationLocation()).setScore(100);



    // 将新的企业信息插入到数据库
    try {
        enterpriseUserMapper.insert(enterprise);
    } catch (Exception e) {
        return Result.fail(ErrorCode.DB_ERROR.getCode(), "企业信息插入失败");
    }

    // 生成 JWT token
    String token = JWTUtils.createToken(newUser.getId());

    // 将 token 和相关企业用户信息存入 Redis
    redisTemplate.opsForValue().set("TOKEN_" + token, JSON.toJSONString(enterpriseUser), 1, TimeUnit.DAYS);

    // 返回成功的结果和 token
    return Result.success(token);
}


        public Result loginOut(String token) {
            redisTemplate.delete("TOKEN_"+token);
            return Result.success(null);
        }

        // 检查token并返回相应的用户信息
        public User checkToken(String token) {
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

    }
