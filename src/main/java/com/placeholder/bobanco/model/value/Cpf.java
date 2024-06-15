package com.placeholder.bobanco.model.value;

import jakarta.persistence.Embeddable;

@Embeddable
public class Cpf {

    private String cpf;

    public Cpf() {
    }

    public Cpf(String cpf) {
        if (cpf == null || !cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
            throw new IllegalArgumentException("Invalid CPF");
        }
        this.cpf = cpf;
    }

    public String getcpf() {
        return cpf;
    }

    public void setcpf(String cpf) {
        this.cpf = cpf;
    }

    @Override
    public String toString() {
        return cpf;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        Cpf other = (Cpf) obj;
        return this.cpf.equals(other.cpf);
    }
    
}
