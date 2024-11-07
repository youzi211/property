package org.yzc.property1.dao.entity;
import java.time.LocalDateTime; // 导入 LocalDateTime 类
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@TableName("dataip") // 映射数据库表
@Accessors(chain = true)// 使 lombok 注解支持链式调用
public class DataIp {
    //标识主键
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String owner;
    private String dataSummary;
    private int initialScore;
    private LocalDateTime registerTime; // 注册时间
    private Boolean isApproved;
}
