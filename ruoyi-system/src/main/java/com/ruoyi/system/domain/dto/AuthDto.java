package com.ruoyi.system.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * []
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/9/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthDto {

    private String authId;


    public Boolean validAuthId(){
        return "bkjkfjiskawquq".equals(authId);
    }
}
