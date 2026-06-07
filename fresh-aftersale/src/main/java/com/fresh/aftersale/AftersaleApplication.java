package com.fresh.aftersale;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@MapperScan("com.fresh.aftersale.mapper")
public class AftersaleApplication {

    public static void main(String[] args) {
        SpringApplication.run(AftersaleApplication.class, args);
    }

}
