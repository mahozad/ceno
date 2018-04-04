package com.uni.ceno;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class CenoApplication {

    // TODO: be able to upload and show either photo or video for the post
    // TODO: privileged users should be able to pin a post, remove a post, or change a post categories

    public static void main(String[] args) {
        SpringApplication.run(CenoApplication.class, args);
    }

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder(5);
    }
}
