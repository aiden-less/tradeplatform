package com.converage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;


@MapperScan("com.converage.**.mapper")
@EnableScheduling
@EnableCaching
@SpringBootApplication
public class BootStrapApplication{
	public static void main(String[] args) {
		SpringApplication.run(BootStrapApplication.class, args);
	}
}
