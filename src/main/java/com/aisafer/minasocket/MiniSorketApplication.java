package com.aisafer.minasocket;

import com.aisafer.minasocket.api.*;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ImportResource;

/**
 * springboot启动类
 *
 * @author weiyuanlong, 2018-06-03 14:46:00
 * @version 1.0, 15012710712
 *
 */
@SpringBootApplication
@EnableAutoConfiguration
@MapperScan(basePackages = {"com.aisafer.minasocket.mapper"})
@ImportResource(locations = {"classpath:application-bean.xml"})
@ServletComponentScan
public class MiniSorketApplication {

    /** 日志 */
    private static Logger log = LoggerFactory.getLogger(MiniSorketApplication.class);

    /**
     * springboot启动方法
     *
     * @param args
     */
    public static void main(String[] args) {

        SpringApplication.run(MiniSorketApplication.class, args);
        // 启动mina服务器
        log.info("mina服务器启动中。。。。。。。。。。。。。");
        InitSorketParam.startMina();
    }

}
