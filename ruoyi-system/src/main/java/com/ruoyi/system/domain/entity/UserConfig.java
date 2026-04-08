package com.ruoyi.system.domain.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


import java.io.Serializable;


/**
* 用户配置表(UserConfig)
*
* @author chenxiangyue
* @version v1.0.0
* @date 2026-04-01 23:29:53
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value ="user_config" )
public class UserConfig extends ByteBaseEntity implements Serializable {
private static final long serialVersionUID = 1L;
/**
    * 主键id
    */
@TableId@TableField(value = "id")
private String id;
/**
    * 用户ID
    */
@TableField(value = "user_id")
private String userId;
/**
    * 到期邮件提醒开关：0-关闭，1-开启
    */
@TableField(value = "expire_email_notice")
private Integer expireEmailNotice;
/**
    * 续费失败邮件提醒开关：0-关闭，1-开启
    */
@TableField(value = "renew_fail_email_notice")
private Integer renewFailEmailNotice;

}
