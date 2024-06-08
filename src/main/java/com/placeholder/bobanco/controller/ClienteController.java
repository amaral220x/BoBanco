package com.placeholder.bobanco.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

import com.placeholder.bobanco.repository.ClienteRepository;
import com.placeholder.bobanco.model.entity.Cliente;

import java.util.List;



@RestController
@RequestMapping("/cliente")
public class ClienteController {

    private final ClienteRepository repository;
    public ClienteController(ClienteRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/")
    public List<Cliente> all() {
        return repository.findAll();
    }
    
}
