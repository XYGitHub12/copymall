package com.macro.copymall.search.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置类
 */
@Configuration
@MapperScan({"com.macro.copymall.mapper","com.macro.copymall.search.dao"})
public class MyBatisConfig {
}
