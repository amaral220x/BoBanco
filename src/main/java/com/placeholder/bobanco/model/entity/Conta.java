package com.placeholder.bobanco.model.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Conta {

    @Id
    @UuidGenerator
    private UUID id;
    private double saldo;
    @OneToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonBackReference
    private Cliente cliente;

    public Conta() {
    }

    public Conta(Cliente cliente) {
        this.id = UUID.randomUUID();
        this.cliente = cliente;
        this.saldo = 0;
    }

    public Conta(double saldo, Cliente cliente) {
        this.id = UUID.randomUUID();
        this.saldo = saldo;
        this.cliente = cliente;
    }

    public UUID getId() {
        return id;
    }

    public double getSaldo() {
        return saldo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void depositar(double valor) {
        this.saldo += valor;
    }

    public boolean sacar(double valor) {
        if (this.saldo < valor) {
            return false;
        }
        this.saldo -= valor;
        return true;
    }    
}
