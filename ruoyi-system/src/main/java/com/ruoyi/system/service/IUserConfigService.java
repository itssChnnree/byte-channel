package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.dto.UserConfigDto;
import com.ruoyi.system.domain.entity.UserConfig;
import com.ruoyi.system.domain.vo.UserConfigVo;


/**
 * 用户配置表(UserConfig)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-04-01 23:27:09
 */
public interface IUserConfigService {

    /**
     * 获取当前登录用户配置
     *
     * @return 用户配置DTO
     */
    UserConfigVo getCurrentUserConfig();

    /**
     * 更新当前登录用户配置
     *
     * @param dto 用户配置DTO
     */
    void updateCurrentUserConfig(UserConfigDto dto);
}
