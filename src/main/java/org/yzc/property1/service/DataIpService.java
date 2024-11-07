package org.yzc.property1.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.yzc.property1.dao.entity.DataIp;
import org.yzc.property1.dao.entity.Enterprise;
import org.yzc.property1.dao.entity.User;
import org.yzc.property1.dao.mapper.DataIpMapper;
import org.yzc.property1.model.DataIPInputBO;
import org.yzc.property1.model.EnterpriseUser;
import org.yzc.property1.model.ErrorCode;
import org.yzc.property1.model.Result;
import org.yzc.property1.model.params.DataIpParam;
import org.yzc.property1.util.HttpUtils;
import org.yzc.property1.util.JWTUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.yzc.property1.model.ErrorCode.PARAMS_ERROR;

@Service
public class DataIpService extends ServiceImpl<DataIpMapper, DataIp> {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    //申请知识产权
    public Result registerProperty(String token, DataIPInputBO dataIPInputBO) {
        //首先根据token获取企业用户地址
        String address = checkToken(token);
        //实体类
        DataIp dataIp = new DataIp();
        dataIp.setOwner(address)
                .setDataSummary(dataIPInputBO.getDataSummary())
                .setIsApproved(false)
                .setRegisterTime(LocalDateTime.now());
        //保存到数据库
        save(dataIp);
        return Result.success("申请成功，等待审核");
    }
    //根据token获取企业用户地址
    private String checkToken(String token) {
        if (StringUtils.isBlank(token)) {
            throw new IllegalArgumentException("Token不能为空");
        }

        Map<String, Object> claims = JWTUtils.checkToken(token);
        if (claims == null) {
            throw new IllegalArgumentException("无效的Token");
        }

        String userJson = redisTemplate.opsForValue().get("TOKEN_" + token);
        if (StringUtils.isBlank(userJson)) {
            throw new IllegalArgumentException("用户信息未找到");
        }

        User user = JSON.parseObject(userJson, User.class);
        if (user == null || StringUtils.isBlank(user.getUserAddress())) {
            throw new IllegalArgumentException("用户地址未找到");
        }

        return user.getUserAddress();
    }

    //管理员审核产权
    public Result updatePropertyStatus(int id, DataIpParam dataIpParam) {
        //根据id查询产权数据
        DataIp dataIp = getById(id);

        if (dataIp == null) {
            return Result.fail(PARAMS_ERROR.getCode(), PARAMS_ERROR.getMsg());
        }
        // 调用合约的方法，将企业信息写入区块链
        List<Object> funcParam = Arrays.asList(
                dataIp.getOwner(),
                dataIp.getDataSummary(),
                dataIpParam.getInitialScore()        );

        JSONObject result = HttpUtils.writeContract("registerAndApproveDataIP", funcParam);
        if (result == null) {
            return Result.fail(ErrorCode.CONTRACT_ERROR.getCode(), "区块链写入失败");
        }
        // 更新数据库
        dataIp.setIsApproved(true).setInitialScore(dataIpParam.getInitialScore());
        updateById(dataIp);
        return Result.success("审核成功");
    }

    public Page<DataIp> getPaginatedPropertyNotFinish(int currentPage, int pageSize) {
        Page<DataIp> page = new Page<>(currentPage, pageSize);
        // 使用LambdaQueryWrapper来设置查询条件
        LambdaQueryWrapper<DataIp> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DataIp::getIsApproved, "false"); // 未审核
        queryWrapper.orderByDesc(DataIp::getRegisterTime); // 按申请时间降序排列
        return this.page(page, queryWrapper);
    }
}