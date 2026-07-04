package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.dto.Socks5ResourcesDto;
import com.ruoyi.system.domain.dto.Socks5ResourcesInsertDto;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.Socks5ResourcesMapper;
import com.ruoyi.system.service.ISocks5ResourcesService;
import com.ruoyi.system.domain.entity.Socks5Resources;
import com.ruoyi.system.domain.vo.Socks5ResourcesVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * socks5资源记录(Socks5Resources)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-06-29 22:23:02
 */
@Service("socks5ResourcesService")
public class Socks5ResourcesServiceImpl implements ISocks5ResourcesService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random random = new Random();

    @Resource
    private Socks5ResourcesMapper socks5ResourcesMapper;

    @Override
    public Result insertSocks5(Socks5ResourcesInsertDto dto) {
        Socks5Resources entity = new Socks5Resources();
        entity.setResourcesIp(dto.getResourcesIp());
        entity.setSocks5Port(dto.getSocks5Port());
        entity.setSocks5UserName(dto.getSocks5UserName());
        entity.setSocks5Password(dto.getSocks5Password());
        entity.setPassword(generateRandomString(10));

        int insert = socks5ResourcesMapper.insert(entity);
        if (insert > 0) {
            return Result.success(entity.getPassword());
        }
        return Result.fail("新增失败");
    }

    private static String generateRandomString(int length) {
        List<Character> collect = random.ints(length, 0, CHARACTERS.length())
                .mapToObj(CHARACTERS::charAt)
                .collect(Collectors.toList());
        return collect.stream().map(String::valueOf).collect(Collectors.joining());
    }

}
