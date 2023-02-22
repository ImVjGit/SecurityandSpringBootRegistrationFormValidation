package com.SpringSecurityClient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
//(scanBasePackages = {"org.springframework.security.crypto.password.PasswordEncoder"})
public class SpringSecurityClientApplication {

    public static void main(String[] args) {

        SpringApplication.run(SpringSecurityClientApplication.class, args);
    }

}
