package com.surest_member_managemant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity(prePostEnabled = true)
@EnableCaching
public class SurestMemberManagemantApplication {

	public static void main(String[] args) {
		SpringApplication.run(SurestMemberManagemantApplication.class, args);
	}

}
