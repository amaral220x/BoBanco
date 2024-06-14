package com.placeholder.bobanco.model.entity;

public class ContaPagamento extends Conta {

    public ContaPagamento() {
    }

    public ContaPagamento(Cliente cliente) {
        super(cliente);
    }

    public ContaPagamento(double saldo, Cliente cliente) {
        super(saldo, cliente);
    }
    
}
