package com.placeholder.bobanco;

import java.security.Security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.web.bind.annotation.RestController;

import com.placeholder.bobanco.model.entity.Cliente;
import com.placeholder.bobanco.model.value.Cpf;
import com.placeholder.bobanco.repository.ClienteRepository;

import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@RestController
public class BobancoApplication {

	static Cpf cpfLogado = null;

	public static void main(String[] args) {
		SpringApplication.run(BobancoApplication.class, args);
	}

	// @GetMapping("/teste")
	// public String hello() {
	// 	return "Hello World!";
	// }

	public static void setClienteLogado(Cpf cpf) {
		cpfLogado = cpf;
	}
	public static Cpf getClienteLogado() {
		return cpfLogado;
	}

}
