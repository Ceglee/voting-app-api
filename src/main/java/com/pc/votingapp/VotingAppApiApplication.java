package com.pc.votingapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class VotingAppApiApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(VotingAppApiApplication.class, args);
    }

}
