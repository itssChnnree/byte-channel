package com.ruoyi.system.service.impl;

import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.dto.UserConfigDto;
import com.ruoyi.system.domain.entity.UserConfig;
import com.ruoyi.system.domain.vo.UserConfigVo;
import com.ruoyi.system.mapper.UserConfigMapper;
import com.ruoyi.system.service.IUserConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 用户配置表(UserConfig)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-04-01 23:27:10
 */
@Service("userConfigService")
public class UserConfigServiceImpl implements IUserConfigService {

    @Resource
    private UserConfigMapper userConfigMapper;

    @Override
    @Transactional
    public UserConfigVo getCurrentUserConfig() {
        String userId = SecurityUtils.getStrUserId();
        UserConfig config = userConfigMapper.selectByUserId(userId);
        if (config == null){
            config = new UserConfig();
            config.setUserId(userId);
            config.setExpireEmailNotice(1);
            config.setRenewFailEmailNotice(1);
            userConfigMapper.insert(config);
        }
        UserConfigVo vo = new UserConfigVo();
        vo.setExpireEmailNotice(config.getExpireEmailNotice());
        vo.setRenewFailEmailNotice(config.getRenewFailEmailNotice());
        return vo;
    }

    @Override
    @Transactional
    public void updateCurrentUserConfig(UserConfigDto dto) {
        String userId = SecurityUtils.getStrUserId();
        UserConfig config = userConfigMapper.selectByUserId(userId);

        if (config != null) {
            // 更新
            config.setExpireEmailNotice(dto.getExpireEmailNotice());
            config.setRenewFailEmailNotice(dto.getRenewFailEmailNotice());
            userConfigMapper.updateById(config);
        } else {
            // 新增
            config = new UserConfig();
            config.setUserId(userId);
            config.setExpireEmailNotice(dto.getExpireEmailNotice());
            config.setRenewFailEmailNotice(dto.getRenewFailEmailNotice());
            userConfigMapper.insert(config);
        }
    }
}
