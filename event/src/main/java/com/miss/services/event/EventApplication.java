package com.miss.services.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EntityScan("com.miss.services.common.domains")
@EnableFeignClients(basePackages = {"com.miss.services.event.services", "com.miss.services.common.apis"})
public class EventApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventApplication.class, args);
    }

}
