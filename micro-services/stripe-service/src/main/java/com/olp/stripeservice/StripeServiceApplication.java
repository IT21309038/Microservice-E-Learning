package com.olp.stripeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class StripeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StripeServiceApplication.class, args);
	}

}
