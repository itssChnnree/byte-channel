package com.ruoyi.system.domain.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;


/**
 * 厂商账号信息表(VendorAccountInformation)ʵ����
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-08 16:47:28
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "vendor_account_information")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorAccountInformation extends ByteBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId
    @TableField(value = "id")
    private String id;
    /**
     * 所属厂家id
     */
    @TableField(value = "vendor_id")
    private String vendorId;
    /**
     * 账号
     */
    @TableField(value = "account")
    private String account;
    /**
     * 密码
     */
    @TableField(value = "password")
    private String password;
    /**
     * 邮箱
     */
    @TableField(value = "vendor_email")
    private String vendorEmail;
    /**
     * 手机号
     */
    @TableField(value = "vendor_phone")
    private String vendorPhone;


}
