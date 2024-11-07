package org.yzc.property1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yzc.property1.dao.entity.User;
import org.yzc.property1.model.DataIPInputBO;
import org.yzc.property1.model.EnterpriseUser;
import org.yzc.property1.model.Result;

import org.yzc.property1.model.params.LoginParam;
import org.yzc.property1.service.DataIpService;
import org.yzc.property1.service.LoginService;
import org.yzc.property1.service.UserService;

import static org.yzc.property1.model.ErrorCode.NO_PERMISSION;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private DataIpService dataIpService;

    @PostMapping("/register")
    public Result register(@RequestBody EnterpriseUser enterpriseUser) {
        return loginService.register(enterpriseUser);
    }

    //login
    @PostMapping("/login")
    public Result login(@RequestBody LoginParam user) {
        return loginService.login(user);
    }

    @GetMapping("/logout")
    public Result logout(@RequestHeader("Authorization") String token) {
        return loginService.loginOut(token);
    }

    @GetMapping("/getEnterpriseInfo")
    public Result getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            User user = loginService.checkToken(token);
            if (user == null) {
                return Result.fail(NO_PERMISSION.getCode(), "用户未登录或无效的令牌");
            }
            if (!user.getRoles().equals("user")) {
                return Result.fail(NO_PERMISSION.getCode(), NO_PERMISSION.getMsg());
            }
            return userService.getUserInfo(token);
        } catch (Exception e) {
            return Result.fail(500, "服务器内部错误");
        }
    }



    //用户注册产权信息
    @PostMapping("/registerProperty")
    public Result registerProperty(@RequestHeader("Authorization") String token, @RequestBody DataIPInputBO dataIPInputBO) {
        try {
            User user = loginService.checkToken(token);
            if (user == null) {
                return Result.fail(NO_PERMISSION.getCode(), "用户未登录或无效的令牌");
            }
            if (!user.getRoles().equals("user")) {
                return Result.fail(NO_PERMISSION.getCode(), NO_PERMISSION.getMsg());
            }
            return dataIpService.registerProperty(token, dataIPInputBO);
        } catch (Exception e) {
            return Result.fail(500, "服务器内部错误");
        }
    }

}