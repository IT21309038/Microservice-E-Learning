package com.ds.userenrolmentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class UserEnrolmentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserEnrolmentServiceApplication.class, args);
    }

}
