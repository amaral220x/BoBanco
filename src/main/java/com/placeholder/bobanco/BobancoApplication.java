package com.placeholder.bobanco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
@RestController
public class BobancoApplication {

	public static void main(String[] args) {
		SpringApplication.run(BobancoApplication.class, args);
	}

	@GetMapping("/teste")
	public String hello() {
		return "Hello World!";
	}

}
