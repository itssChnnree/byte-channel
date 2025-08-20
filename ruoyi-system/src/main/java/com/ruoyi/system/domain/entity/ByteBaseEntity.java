package com.ruoyi.system.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


/**
 * []
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/7/30
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ByteBaseEntity {

    /**
     * 创建人
     */
    @TableField(value = "id",fill = FieldFill.INSERT)
    private String id;
    /**
     * 创建人
     */
    @TableField(value = "create_user",fill = FieldFill.INSERT  )
    private String createUser;
    /**
     * 修改人
     */
    @TableField(value = "update_user",fill = FieldFill.INSERT_UPDATE  )
    private String updateUser;
    /**
     * 创建时间
     */
    @TableField(value = "create_time",fill = FieldFill.INSERT  )
    private Date createTime;
    /**
     * 修改时间
     */
    @TableField(value = "update_time",fill = FieldFill.INSERT_UPDATE  )
    private Date updateTime;

    /**
     * 为未删除，1为已删除
     */

    @TableField(value = "is_deleted",fill = FieldFill.INSERT)
    private Integer isDeleted;
    /**
     * 状态
     */
    @Getter
    @TableField(value = "status")
    private String status;



}
