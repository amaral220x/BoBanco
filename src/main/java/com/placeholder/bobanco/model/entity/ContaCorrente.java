package com.placeholder.bobanco.model.entity;

import jakarta.persistence.Entity;

@Entity
public class ContaCorrente extends Conta {

    private double limiteChequeEspecial;

    public ContaCorrente() {
    }

    public ContaCorrente(Cliente cliente, double limiteChequeEspecial) {
        super(cliente);
        this.limiteChequeEspecial = limiteChequeEspecial;
    }

    public ContaCorrente(double saldo, Cliente cliente, double limiteChequeEspecial) {
        super(saldo, cliente);
        this.limiteChequeEspecial = limiteChequeEspecial;
    }

    public double getLimiteChequeEspecial() {
        return limiteChequeEspecial;
    }

    public void setLimiteChequeEspecial(double limiteChequeEspecial) {
        this.limiteChequeEspecial = limiteChequeEspecial;
    }

    public boolean transferencia(Conta contaDestino, double valor) {
        if (valor > this.getSaldo() + this.limiteChequeEspecial) {
            return false;
        }
        this.sacar(valor);
        contaDestino.depositar(valor);
        return true;
    }

    @Override
    public boolean sacar(double valor) {
        if (valor > this.getSaldo() + this.limiteChequeEspecial) {
            return false;
        }
        this.setSaldo(this.getSaldo() - valor);
        return true;
    }



}