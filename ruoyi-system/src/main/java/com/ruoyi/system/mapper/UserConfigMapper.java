package com.ruoyi.system.mapper;



import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.UserConfig;

/**
 * 用户配置表(UserConfig
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-04-01 23:29:53
 */
@Mapper
@Repository
public interface UserConfigMapper extends BaseMapper<UserConfig> {

    /**
     * 根据用户ID查询配置
     *
     * @param userId 用户ID
     * @return 用户配置
     */
    @Select("SELECT * FROM user_config WHERE user_id = #{userId} and is_deleted = 0")
    UserConfig selectByUserId(String userId);
}
