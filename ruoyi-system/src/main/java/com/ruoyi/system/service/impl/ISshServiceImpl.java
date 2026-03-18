package com.ruoyi.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.dto.SshTemporaryDto;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.ISshService;
import org.springframework.beans.factory.annotation.Value;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.stereotype.Service;

/**
 * [查询ssh临时连接]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/3/14
 */
@Service
public class ISshServiceImpl implements ISshService {

    @Resource(name = "sshTemporaryDtoCache")
    private Cache<String, List<SshTemporaryDto>> cache;

    // 令牌自定义标识
    @Value("${token.header}")
    private String header;

    public String getToken(HttpServletRequest request)
    {
        String token = request.getHeader(header);
        return getToken(token);
    }

    public String getToken(String token){
        if (StringUtils.isNotEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX))
        {
            token = token.replace(Constants.TOKEN_PREFIX, "");
        }
        return token;
    }

    /**
     * [创建ssh临时缓存信息]
     *
     * @param sshTemporaryDto 临时缓存
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2026/3/14
     **/
    @Override
    public Result addTemporarySsh(SshTemporaryDto sshTemporaryDto, HttpServletRequest request) {
        String token = getToken(request);
        addTemporarySsh(sshTemporaryDto, token);
        return Result.success();
    }

    /**
     * [创建ssh临时缓存信息]
     *
     * @param sshTemporaryDto 临时缓存
     * @param token
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2026/3/14
     **/
    @Override
    public void addTemporarySsh(SshTemporaryDto sshTemporaryDto, String token) {
        //根据token获取集合，存在则获取
        List<SshTemporaryDto> sshTemporaryDtos = cache.get(token, s -> new ArrayList<>());
        AtomicReference<SshTemporaryDto> oldSsh = new AtomicReference<>();
        sshTemporaryDtos.stream().forEach(ssh -> {
            if (sshTemporaryDto.getIp().equals(ssh.getIp())){
                oldSsh.set(ssh);
            }
        });
        if (oldSsh.get() == null){
            sshTemporaryDtos.add(sshTemporaryDto);
        }else {
            SshTemporaryDto sshTemporaryDto1 = oldSsh.get();
            sshTemporaryDto1.setPort(sshTemporaryDto.getPort());
            sshTemporaryDto1.setUsername(sshTemporaryDto.getUsername());
            sshTemporaryDto1.setPassword(sshTemporaryDto.getPassword());
        }
    }

    /**
     * [通过token获取ssh信息]
     *
     * @param request 缓存token
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2026/3/14
     **/
    @Override
    public Result getByToken(HttpServletRequest request) {
        String token = getToken(request);
        List<SshTemporaryDto> sshTemporaryDtos = cache.get(token, s -> new ArrayList<>());
        if (CollectionUtil.isEmpty(sshTemporaryDtos)){
            return Result.success();
        }
        List<String> ipList = sshTemporaryDtos.stream().map(SshTemporaryDto::getIp).collect(Collectors.toList());
        return Result.success(ipList);
    }

    public SshTemporaryDto getByToken(String token,String ip){
        if (StringUtils.isEmpty(token) || StringUtils.isEmpty(ip)){
            return null;
        }
        token = getToken(token);
        List<SshTemporaryDto> sshTemporaryDtos = cache.getIfPresent(token);
        if (CollectionUtil.isEmpty(sshTemporaryDtos)){
            return null;
        }

        for (SshTemporaryDto sshTemporaryDto : sshTemporaryDtos){
            if (sshTemporaryDto.getIp().equals(ip)){
                return sshTemporaryDto;
            }
        }
        return null;
    }
}
