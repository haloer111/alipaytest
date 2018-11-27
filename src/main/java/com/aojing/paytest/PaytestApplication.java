package com.aojing.paytest;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.aojing.paytest.mapper")
public class PaytestApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaytestApplication.class, args);
    }
}
