package com.ead.notificationhex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class EadNotificationHexApplication {

    public static void main(String[] args){
        SpringApplication.run(EadNotificationHexApplication.class, args);
    }
}
