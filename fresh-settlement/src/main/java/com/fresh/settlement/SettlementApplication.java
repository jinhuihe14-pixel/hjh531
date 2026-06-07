package com.fresh.settlement;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@MapperScan("com.fresh.settlement.mapper")
public class SettlementApplication {

    public static void main(String[] args) {
        SpringApplication.run(SettlementApplication.class, args);
    }

}
