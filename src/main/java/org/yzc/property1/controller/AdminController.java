package org.yzc.property1.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yzc.property1.dao.entity.DataIp;
import org.yzc.property1.dao.entity.Enterprise;
import org.yzc.property1.dao.entity.User;
import org.yzc.property1.model.EnterpriseUser;
import org.yzc.property1.model.Result;
import org.yzc.property1.model.params.DataIpParam;
import org.yzc.property1.service.DataIpService;
import org.yzc.property1.service.EnterpriseService;
import org.yzc.property1.service.LoginService;

import static org.yzc.property1.model.ErrorCode.NO_PERMISSION;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private EnterpriseService enterpriseService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private DataIpService dataIpService;
    //管理员查找所有企业
    @GetMapping("/enterprises")
    public Result getEnterprises(@RequestHeader("Authorization") String token,
                                                   @RequestParam(defaultValue = "1") int currentPage,
                                                   @RequestParam(defaultValue = "10") int pageSize) {
        try {
            User user = loginService.checkToken(token);
            if (user == null) {
                return Result.fail(NO_PERMISSION.getCode(), "用户未登录或无效的令牌");
            }
            if (!user.getRoles().equals("admin")) {
                return Result.fail(NO_PERMISSION.getCode(), NO_PERMISSION.getMsg());
            }
            Page<Enterprise> page = enterpriseService.getPaginatedEnterprises(currentPage, pageSize);
            return Result.success(page);
        } catch (Exception e) {
            // 记录异常日志（假设有日志记录的功能）
            // logger.error("获取企业列表失败", e);
            return Result.fail(500, "服务器内部错误");
        }
    }
        //管理员审核企业
        @PutMapping("/enterprises/{id}")
        public Result updateEnterpriseStatus(@RequestHeader("Authorization") String token, @PathVariable("id") int id) {
            try {
                User user = loginService.checkToken(token);
                if (user == null) {
                    return Result.fail(NO_PERMISSION.getCode(), "用户未登录或无效的令牌");
                }
                if (!user.getRoles().equals("admin")) {
                    return Result.fail(NO_PERMISSION.getCode(), NO_PERMISSION.getMsg());
                }
               Result result = enterpriseService.updateEnterpriseStatus(id);
                return Result.success(result);
            } catch (Exception e) {
                // 记录异常日志（假设有日志记录的功能）
                // logger.error("更新企业状态失败", e);
                return Result.fail(500, "服务器内部错误");
            }
        }
        //管理员获取所有待审核的产权列表
        @GetMapping("/enterprises/propertyNotFinish")
        public Result getPropertyNotFinish(@RequestHeader("Authorization") String token,
                                                   @RequestParam(defaultValue = "1") int currentPage,
                                                   @RequestParam(defaultValue = "10") int pageSize) {
            try {
                User user = loginService.checkToken(token);
                if (user == null) {
                    return Result.fail(NO_PERMISSION.getCode(), "用户未登录或无效的令牌");
                }
                if (!user.getRoles().equals("admin")) {
                    return Result.fail(NO_PERMISSION.getCode(), NO_PERMISSION.getMsg());
                }
                Page<DataIp> page = dataIpService.getPaginatedPropertyNotFinish(currentPage, pageSize);
                return Result.success(page);
            } catch (Exception e) {
                // 记录异常日志（假设有日志记录的功能）
                // logger.error("获取待审核的产权列表失败", e);
                return Result.fail(500, "服务器内部错误");
            }
        }

        //管理员审核知识产权
        @PutMapping("/enterprises/{id}/property")
        public Result updatePropertyStatus(@RequestHeader("Authorization") String token, @RequestBody DataIpParam dataIpParam, @PathVariable("id") int id) {
            try {
                User user = loginService.checkToken(token);
                if (user == null) {
                    return Result.fail(NO_PERMISSION.getCode(), "用户未登录或无效的令牌");
                }
                if (!user.getRoles().equals("admin")) {
                    return Result.fail(NO_PERMISSION.getCode(), NO_PERMISSION.getMsg());
                }
                Result result = dataIpService.updatePropertyStatus(id, dataIpParam);
                return Result.success(result);
            } catch (Exception e) {
                // 记录异常日志（假设有日志记录的功能）
                // logger.error("更新企业知识产权状态失败", e);
                return Result.fail(500, "服务器内部错误");
            }
        }



}

