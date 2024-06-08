package com.placeholder.bobanco.repository;

import com.placeholder.bobanco.model.entity.Cliente;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, UUID>{

    
}
