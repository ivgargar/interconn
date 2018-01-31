package com.ryanair.test.interconn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.ryanair.test.interconn")
public class InterconnApplication {

	public static void main(String[] args) {
		SpringApplication.run(InterconnApplication.class, args);
	}
}
