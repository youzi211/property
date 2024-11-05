package org.yzc.property1.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.yzc.property1.dao.entity.Enterprise;
import org.yzc.property1.dao.mapper.EnterpriseUserMapper;
import org.yzc.property1.model.ErrorCode;
import org.yzc.property1.model.Result;
import org.yzc.property1.util.HttpUtils;

import java.util.Arrays;
import java.util.List;

@Service
public class EnterpriseService extends ServiceImpl<EnterpriseUserMapper, Enterprise> {

    public Page<Enterprise> getPaginatedEnterprises(int currentPage, int pageSize) {
        Page<Enterprise> page = new Page<>(currentPage, pageSize);
        return this.page(page);
    }

    //管理员审核企业
    public Result updateEnterpriseStatus(int id) {
        Enterprise enterprise = this.getById(id);

            // 调用合约的方法，将企业信息写入区块链
          List<Object> funcParam = Arrays.asList(
                  enterprise.getUserAddress(),
                  enterprise.getEnterpriseName(),
                  enterprise.getDomesticOrForeign(),
                  enterprise.getRegistrationLocation(),
                  enterprise.getForeignInvestmentRatio(),
                  enterprise.getFinancingScale()
          );

    JSONObject result = HttpUtils.writeContract("registerEnterpriseUser", funcParam);
    if (result == null) {
        return Result.fail(ErrorCode.CONTRACT_ERROR.getCode(), "区块链写入失败");
    }

       if (!enterprise.getIsRegistered()){
            enterprise.setIsRegistered(true);
            this.updateById(enterprise);}

            return Result.success("企业注册成功");

    }
}