package com.placeholder.bobanco.model.value;

import jakarta.persistence.Embeddable;

@Embeddable
public class Email {

    private String email;

    public Email() {
    }

    public Email(String email) {
        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if (email == null || !email.matches(regex)) {
            throw new IllegalArgumentException("Invalid Email");
        }
        this.email = email;
    }

    public String getemail() {
        return email;
    }

    public void setemail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return email;
    }
    
}
