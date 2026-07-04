package com.ruoyi.system.mapper;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.Socks5Resources;

/**
 * socks5资源记录(Socks5Resources
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-06-29 22:23:01
 */
@Mapper
@Repository
public interface Socks5ResourcesMapper extends BaseMapper<Socks5Resources> {

    default Socks5Resources selectByPassword(String password) {
        return selectOne(new LambdaQueryWrapper<Socks5Resources>()
                .eq(Socks5Resources::getPassword, password));
    }
}
