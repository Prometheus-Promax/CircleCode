package org.bteam.circlecode;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.bteam.circlecode.mapper")
public class CircleCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CircleCodeApplication.class, args);
    }

}
