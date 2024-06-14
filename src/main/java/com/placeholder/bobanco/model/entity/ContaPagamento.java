package com.placeholder.bobanco.model.entity;

public class ContaPagamento extends Conta {

    private int transferenciaNoMes;

    public ContaPagamento() {
    }

    public ContaPagamento(Cliente cliente) {
        super(cliente);
        transferenciaNoMes = 0;
    }

    public ContaPagamento(double saldo, Cliente cliente) {
        super(saldo, cliente);
    }

    public boolean transferencia(Conta contaDestino, double valor) {
        if(valor > 4999.99 || valor < 0){
            throw new IllegalArgumentException("Valor inválido");
        }
        if (valor > this.getSaldo() ) {
            return false;
        }
        this.sacar(valor);
        contaDestino.depositar(valor);
        return true;
    }

    @Override
    public void sacar(double valor) {
        if(transferenciaNoMes > 5){
            if(valor > this.getSaldo()){
                throw new IllegalArgumentException("Saldo insuficiente");
            }
            this.setSaldo(this.getSaldo() - valor);
            transferenciaNoMes++;
            return;
        }
        if (valor > this.getSaldo() + 6.5) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }
        this.setSaldo(this.getSaldo() - valor + 6.5);
    }
    
}
