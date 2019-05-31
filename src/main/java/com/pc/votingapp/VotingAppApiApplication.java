package com.pc.votingapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class VotingAppApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(VotingAppApiApplication.class, args);
    }

}
