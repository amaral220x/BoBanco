package com.placeholder.bobanco.repository;

import com.placeholder.bobanco.model.entity.Cliente;
import com.placeholder.bobanco.model.value.Cpf;
import com.placeholder.bobanco.model.value.Email;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, UUID>{
    Optional<Cliente> findByCpf(Cpf cpf);

    
    Optional<Cliente> findByEmail(Email email);

}
