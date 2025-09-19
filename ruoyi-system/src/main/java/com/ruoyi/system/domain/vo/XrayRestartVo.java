package com.ruoyi.system.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [Xray节点反参]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/9/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class XrayRestartVo {


    private String privateKey;

    private String publicKey;

    private String shortId;
}
