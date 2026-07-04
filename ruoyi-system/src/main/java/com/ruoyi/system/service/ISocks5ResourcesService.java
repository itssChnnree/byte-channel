package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.dto.Socks5ResourcesDto;
import com.ruoyi.system.domain.dto.Socks5ResourcesInsertDto;
import com.ruoyi.system.domain.entity.Socks5Resources;
import com.ruoyi.system.domain.vo.Socks5ResourcesVo;
import com.ruoyi.system.http.Result;


/**
 * socks5资源记录(Socks5Resources)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-06-29 22:23:02
 */
public interface ISocks5ResourcesService {

    /**
     * [第三方上传socks5配置]
     * @param dto 上传参数
     * @return 生成的查询密码
     * @author chenxiangyue 2026/7/4
     */
    Result insertSocks5(Socks5ResourcesInsertDto dto);

}
