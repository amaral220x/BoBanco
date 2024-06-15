package com.placeholder.bobanco.model.entity;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.CascadeType;

import org.hibernate.annotations.UuidGenerator;
import org.hibernate.mapping.List;

import com.placeholder.bobanco.model.value.Cpf;
import com.placeholder.bobanco.model.value.Email;
import com.placeholder.bobanco.utils.SenhaUtils;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.placeholder.bobanco.exception.ClienteException;

import java.util.UUID;
import java.util.Map;
import java.util.ArrayList;


@Entity
@Table(name = "cliente")
public class Cliente {


    @Id
    @UuidGenerator
    private UUID id;
    @Embedded
    @Column(name = "cpf", unique = true)
    private Cpf cpf;
    String nome;
    @Embedded
    @Column(name = "email", unique = true)
    private Email email;
    private String senha;
    private String endereco; 
    private double rendaMensal;
    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "conta_id")
    @JsonManagedReference
    private Conta conta;
    private ArrayList<Map <String, String>> transacoes = new ArrayList<Map<String, String>>();

    public Cliente() {
    }

    public Cliente(Cpf cpf, String nome, Email email, String senha ,String endereco, double rendaMensal) {
        if(!SenhaUtils.senhaValida(senha)){
            throw new ClienteException.InvalidPasswordException();
        }
        this.id = UUID.randomUUID();
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.senha = SenhaUtils.criptografarSenha(senha);
        this.endereco = endereco;
        this.rendaMensal = rendaMensal;
    }

    public String getSenha() {
        return senha;
    }

    public UUID getId() {
        return id;
    }

    public Cpf getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public Email getEmail() {
        return email;
    }

    public String getEndereco() {
        return endereco;
    }

    public double getRendaMensal() {
        return rendaMensal;
    }

    public ArrayList<Map<String, String>> getTransacoes() {
        return transacoes;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }



    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setRendaMensal(double rendaMensal) {
        this.rendaMensal = rendaMensal;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public void setCpf(Cpf cpf) {
        this.cpf = cpf;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }


    @Override
    public String toString() {
        return "Cliente [cpf=" +cpf+ ", email=" + email +", endereco=" + endereco + ", nome=" + nome + ", rendaMensal="
                + rendaMensal + "]";
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public Conta getConta() {
        return conta;
    }

    public void addTransacao(String identificador, String valor){
        Map<String, String> transacao = Map.of(identificador, valor);
        this.transacoes.add(transacao);
    }
}
