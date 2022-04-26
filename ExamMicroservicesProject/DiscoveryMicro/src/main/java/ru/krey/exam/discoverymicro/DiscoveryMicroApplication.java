package ru.krey.exam.discoverymicro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class DiscoveryMicroApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryMicroApplication.class, args);
    }

}
