package org.yzc.property1.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@TableName("enterpriseuser") // 映射数据库表
@Accessors(chain = true)// 使 lombok 注解支持链式调用
public class Enterprise {

    @TableId(type = IdType.AUTO)
    private Integer id; // 主键
    private String userAddress; // 用户地址
    private String enterpriseName; // 企业名称
    private int domesticOrForeign; // 国内或国外 (0: 国内, 1: 国外)
    private String registrationLocation; // 注册地点
    private int foreignInvestmentRatio; // 外资比例
    private Long financingScale; // 融资规模
    private Boolean isRegistered; // 是否注册
    private int score; // 评分
    private int overdueRepaymentCount; //  逾期还款次数

}