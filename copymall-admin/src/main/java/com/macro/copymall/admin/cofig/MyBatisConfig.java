package com.macro.copymall.admin.cofig;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis配置类
 */
@Configuration
@EnableTransactionManagement
@MapperScan({"com.macro.copymall.mbg.mapper","com.macro.copymall.admin.dao"})
public class MyBatisConfig {
}
