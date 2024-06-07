package com.placeholder.bobanco.repository;

import java.util.UUID;

import com.placeholder.bobanco.model.entity.Teste;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TesteRepository extends JpaRepository<Teste, UUID>{
    
}
