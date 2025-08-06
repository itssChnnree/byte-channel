package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
* 故障处理流程表(FaultHandling)ʵ����
*
* @author chenxiangyue
* @version v1.0.0
* @date 2025-07-20 23:23:53
*/
@TableName(value ="fault_handling" )
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class FaultHandling  extends ByteBaseEntity implements Serializable {
private static final long serialVersionUID = 1L;

    /**
        * 用户id
        */
    @TableField(value = "user_id")
    private String userId;
    /**
        * 资源id
        */
    @TableField(value = "resources_id")
    private String resourcesId;
    /**
        * 故障描述
        */
    @TableField(value = "fault_description")
    private String faultDescription;

    /**
        * 0为未删除，1为已删除
        */
    @TableField(value = "is_deleted")
    private Integer isDeleted;


}
