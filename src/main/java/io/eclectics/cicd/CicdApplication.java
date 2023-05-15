package io.eclectics.cicd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CicdApplication {
	public static void main(String[] args) {
		SpringApplication.run(CicdApplication.class, args);
	}

}
