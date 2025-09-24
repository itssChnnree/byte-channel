package com.ruoyi.system.domain.entity.XrayOutbound;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * []
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/9/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VNext {

    private String address;
    private int port;
    private List<User> users;
}
