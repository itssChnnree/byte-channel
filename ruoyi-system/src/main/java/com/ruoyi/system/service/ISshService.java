package com.ruoyi.system.service;

import com.ruoyi.system.domain.dto.SshTemporaryDto;
import com.ruoyi.system.http.Result;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

public interface ISshService {


    /**
     * [创建ssh临时缓存信息]
     * @author 陈湘岳 2026/3/14
     * @param sshTemporaryDto 临时缓存
     * @return com.ruoyi.system.http.Result
     **/
    Result addTemporarySsh(SshTemporaryDto sshTemporaryDto, HttpServletRequest request);

    /**
     * [创建ssh临时缓存信息]
     * @author 陈湘岳 2026/3/14
     * @param sshTemporaryDto 临时缓存
     * @return com.ruoyi.system.http.Result
     **/
    void addTemporarySsh(SshTemporaryDto sshTemporaryDto, String token);

    /**
     * [通过token获取ssh信息]
     * @author 陈湘岳 2026/3/14
     * @param request 请求
     * @return com.ruoyi.system.http.Result
     **/
    Result getByToken(HttpServletRequest request);

    /**
     * [通过token获取ssh信息]
     * @author 陈湘岳 2026/3/14
     * @param token token
     * @param ip ip
     * @return com.ruoyi.system.domain.dto.SshTemporaryDto
     **/
    SshTemporaryDto getByToken(String token,String ip);

    /**
     * [获取转化后的token]
     * @author 陈湘岳 2026/3/14
     * @param token token
     * @return java.lang.String
     **/
    String getToken(String token);
}
