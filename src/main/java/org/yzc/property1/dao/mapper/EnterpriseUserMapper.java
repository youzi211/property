package org.yzc.property1.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.yzc.property1.dao.entity.Enterprise;

@Mapper // 指定这是一个 Mapper 接口
public interface EnterpriseUserMapper extends BaseMapper<Enterprise> {
    // 你可以在这里定义额外的查询或操作方法
    //根据地址查询企业
    @Select("SELECT * FROM enterpriseuser WHERE user_address = #{address}")
    Enterprise selectByAddress(String address);
}