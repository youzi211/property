package org.yzc.property1.model;

import lombok.Data;

@Data
public class EnterpriseUser {
    private String username; // 用户名
    private String password; // 密码
    private String enterpriseName; // 企业名称
    private int domesticOrForeign; // 国内或国外 (0: 国内, 1: 国外)
    private String registrationLocation; // 注册地点
    private int foreignInvestmentRatio; // 外资比例（百分比，0到100之间）
    private long financingScale; // 融资规模数字

    // 可以添加其他方法、构造函数等
}