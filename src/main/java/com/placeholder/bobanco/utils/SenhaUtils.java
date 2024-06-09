package com.placeholder.bobanco.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SenhaUtils {
    
    public static boolean senhaValida(String senha) {
        return senha.length() >= 6;
    }

    public static String criptografarSenha(String senha) {
        return new BCryptPasswordEncoder().encode(senha);
    }

    public static boolean senhaCorreta(String senha, String senhaCriptografada) {
        return new BCryptPasswordEncoder().matches(senha, senhaCriptografada);
    }
}
