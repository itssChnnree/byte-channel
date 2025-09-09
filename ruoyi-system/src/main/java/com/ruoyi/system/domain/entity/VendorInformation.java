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
 * 厂商信息表(VendorInformation)ʵ����
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-08 16:47:34
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "vendor_information")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorInformation extends ByteBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId
    @TableField(value = "id")
    private String id;
    /**
     * 厂商名
     */
    @TableField(value = "vendor_name")
    private String vendorName;
    /**
     * 厂商网址
     */
    @TableField(value = "vendor_url")
    private String vendorUrl;
    /**
     * 厂商描述
     */
    @TableField(value = "vendor_description")
    private String vendorDescription;


}
