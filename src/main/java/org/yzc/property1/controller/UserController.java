package org.yzc.property1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yzc.property1.model.EnterpriseUser;
import org.yzc.property1.model.Result;

import org.yzc.property1.model.params.LoginParam;
import org.yzc.property1.service.LoginService;
import org.yzc.property1.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private LoginService loginService;

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
        return userService.getUserInfo(token);
    }


}