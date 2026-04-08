package com.ruoyi.system.domain.vo;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;


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
public class UserConfigVo{

    @ApiModelProperty("主键id")
   private String id;
    
    @ApiModelProperty("用户ID")
   private String userId;
    
    @ApiModelProperty("到期邮件提醒开关：0-关闭，1-开启")
   private Integer expireEmailNotice;
    
    @ApiModelProperty("续费失败邮件提醒开关：0-关闭，1-开启")
   private Integer renewFailEmailNotice;

}
