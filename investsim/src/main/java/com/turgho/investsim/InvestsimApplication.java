package com.turgho.investsim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.dfiney")
public class InvestsimApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvestsimApplication.class, args);
	}

}
