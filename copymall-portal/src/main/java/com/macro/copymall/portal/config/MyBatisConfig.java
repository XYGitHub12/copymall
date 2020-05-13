package com.macro.copymall.portal.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis配置类
 * Created by macro on 2019/4/8.
 */
@Configuration
@EnableTransactionManagement
@ComponentScan("com.macro.copymall.security.*")
@MapperScan({"com.macro.copymall.mbg.mapper","com.macro.copymall.portal.dao"})
public class MyBatisConfig {
}
