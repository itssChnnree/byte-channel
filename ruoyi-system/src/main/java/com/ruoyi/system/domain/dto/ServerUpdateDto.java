package com.ruoyi.system.domain.dto;

import com.ruoyi.system.group.InsertGroup;
import com.ruoyi.system.group.UpdateGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;


/**
 * 服务器资源表(ServerResources)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerUpdateDto {

    @ApiModelProperty("主键id")
    @NotBlank(message = "主键id不能为空",
            groups = {UpdateGroup.class})
    private String id;
    
    @ApiModelProperty("服务器ip")
    @Pattern(
            regexp = "^(?:(?:25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\\.){3}(?:25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])$",
            message = "必须是有效的IPv4地址")
    @NotBlank(message = "服务器ip不能为空")
    private String resourcesIp;

    @ApiModelProperty("代理节点端口")
    private String nodePort;

}
