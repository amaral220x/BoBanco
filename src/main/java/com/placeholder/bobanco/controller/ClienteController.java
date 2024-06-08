package com.placeholder.bobanco.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.placeholder.bobanco.repository.ClienteRepository;
import com.placeholder.bobanco.exception.ClienteException;
import com.placeholder.bobanco.model.entity.Cliente;
import com.placeholder.bobanco.model.value.Cpf;
import com.placeholder.bobanco.model.value.Email;

import java.util.List;
import java.util.Optional; 
import java.util.UUID;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    private final ClienteRepository repository;
    public ClienteController(ClienteRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/")
    public List<Cliente> all() {
        return repository.findAll();
    }
    @GetMapping("/cpf/{cpf}")
    public Cliente one(@PathVariable String cpf) {
        Cpf cpfObj = new Cpf(cpf); 
        Optional<Cliente> cliente = repository.findByCpf(cpfObj); // Store the Optional object
        return cliente.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }
    @GetMapping("/email/{email}")
    public Cliente oneByEmail(@PathVariable String email) {
        Email emailObj = new Email(email);
        Optional<Cliente> cliente = repository.findByEmail(emailObj); // Store the Optional object
        return cliente.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }
    @GetMapping("/id/{id}")
    public Cliente oneById(@PathVariable UUID id) {
        Optional<Cliente> cliente = repository.findById(id); // Store the Optional object
        return cliente.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    @PostMapping("/")
    public ResponseEntity<Object> create(@RequestBody Cliente clienteBody){
        boolean existsCpf = repository.findByCpf(clienteBody.getCpf()).isPresent();
        boolean existsEmail = repository.findByEmail(clienteBody.getEmail()).isPresent();
        if(existsCpf || existsEmail){
            System.out.println("Cliente já cadastrado");
            String erroMessage = new ClienteException.DuplicateClienteException().getMessage();
            return new ResponseEntity<>(erroMessage, HttpStatus.BAD_REQUEST);
        }
        String cpf = clienteBody.getCpf().getcpf();
        if(cpf == null || !cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")){
            System.out.println("CPF inválido");
            String erroMessage = "Invalid CPF";
            return new ResponseEntity<>(erroMessage, HttpStatus.BAD_REQUEST);
        }
        String email = clienteBody.getEmail().getemail();
        if(email == null || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")){
            System.out.println("Email inválido");
            String erroMessage = "Invalid Email";
            return new ResponseEntity<>(erroMessage, HttpStatus.BAD_REQUEST);
        }
        Cliente cliente = repository.save(clienteBody);
        return new ResponseEntity<>(cliente, HttpStatus.CREATED);
    }


        
}
