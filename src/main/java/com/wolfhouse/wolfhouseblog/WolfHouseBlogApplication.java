package com.wolfhouse.wolfhouseblog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author rylinwolf
 */
@SpringBootApplication
@MapperScan("com.wolfhouse.wolfhouseblog.mapper")
@ConfigurationPropertiesScan
@EnableScheduling
public class WolfHouseBlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(WolfHouseBlogApplication.class, args);
    }

}
