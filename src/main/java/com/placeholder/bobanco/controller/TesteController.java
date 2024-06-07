package com.placeholder.bobanco.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.placeholder.bobanco.repository.TesteRepository;

import java.util.List;

import com.placeholder.bobanco.model.entity.Teste;
@RestController
@RequestMapping("/teste")
public class TesteController {

    private final TesteRepository repository;

    public TesteController(TesteRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/all")
    public List<Teste> all() {
        return repository.findAll();
    }


    
}
