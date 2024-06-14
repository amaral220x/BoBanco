package com.placeholder.bobanco.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.placeholder.bobanco.model.entity.Conta;

import java.util.UUID;

public interface ContaRepository extends JpaRepository<Conta, UUID> {
    
}
