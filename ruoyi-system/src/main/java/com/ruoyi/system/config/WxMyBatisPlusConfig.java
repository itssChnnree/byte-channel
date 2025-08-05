package com.ruoyi.system.config;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * MyBatisPlus配置类
 *
 * @author zl 2024/11/13
 * @version 1.0
 */

@Configuration
public class WxMyBatisPlusConfig {


    /**
      * 自动填充处理类
     * @return FillMetaObjectHandle
     */
    @Bean
    @Primary
    public FillMetaObjectHandle fillMetaObjectHandle() {
        FillMetaObjectHandle fillMetaObjectHandle = new FillMetaObjectHandle();
        return fillMetaObjectHandle;
    }

//    @Bean
//    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
//        MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
//        //获取mybatis-plus全局配置
//        GlobalConfig globalConfig = GlobalConfigUtils.defaults();
//        //mybatis-plus全局配置设置元数据对象处理器为自己实现的那个
//        globalConfig.setMetaObjectHandler(fillMetaObjectHandle());
//        mybatisSqlSessionFactoryBean.setDataSource(dataSource);
//        //mybatisSqlSessionFactoryBean关联设置全局配置
//        mybatisSqlSessionFactoryBean.setGlobalConfig(globalConfig);
//        return mybatisSqlSessionFactoryBean.getObject();
//    }

}
