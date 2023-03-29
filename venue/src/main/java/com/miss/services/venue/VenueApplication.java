package com.miss.services.venue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EntityScan("com.miss.services.common.domains")
@EnableFeignClients(basePackages = {"com.miss.services.venue.services", "com.miss.services.common.apis"})
public class VenueApplication {

	public static void main(String[] args) {
		SpringApplication.run(VenueApplication.class, args);
	}

}
