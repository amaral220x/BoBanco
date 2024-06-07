package com.placeholder.bobanco.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "teste")
public class Teste {

    @Id
    @UuidGenerator
    private UUID id;

    public Teste() {
        this.id = UUID.randomUUID();
    }
    

    public UUID getId() {
        return id;
    }
}
