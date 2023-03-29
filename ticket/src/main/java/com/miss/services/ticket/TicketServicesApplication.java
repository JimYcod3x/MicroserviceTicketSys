package com.miss.services.ticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EntityScan("com.miss.services.common.domains")
@EnableFeignClients(basePackages = {"com.miss.services.ticket.services", "com.miss.services.common.apis"})
public class TicketServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketServicesApplication.class, args);
	}

}
