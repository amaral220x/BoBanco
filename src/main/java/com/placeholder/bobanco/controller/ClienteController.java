package com.placeholder.bobanco.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.placeholder.bobanco.repository.ClienteRepository;
import com.placeholder.bobanco.utils.SenhaUtils;
import com.placeholder.bobanco.BobancoApplication;
import com.placeholder.bobanco.exception.ClienteException;
import com.placeholder.bobanco.model.entity.Cliente;
import com.placeholder.bobanco.model.value.Cpf;
import com.placeholder.bobanco.model.value.Email;


import java.util.List;
import java.lang.reflect.Field;
import java.util.Optional; 
import java.util.UUID;
import java.util.Map;

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
    @GetMapping("/logado")
    public ResponseEntity<Object> logado(){
        if(BobancoApplication.getClienteLogado() == null){
            Map<String, String> response = Map.of("message", "Cliente não logado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        Map<String, String> response = Map.of("message", "Cliente logado" + BobancoApplication.getClienteLogado().getNome());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/logout")
    public ResponseEntity<Object> logout(){
        if(BobancoApplication.getClienteLogado() == null){
            Map<String, String> response = Map.of("message", "Cliente não logado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        BobancoApplication.setClienteLogado(null);
        Map<String, String> response = Map.of("message", "Cliente deslogado");
        return new ResponseEntity<>(response, HttpStatus.OK);
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
        String senha = clienteBody.getSenha();
        System.out.println(senha);
        if(senha == null || !SenhaUtils.senhaValida(senha)){
            System.out.println("Senha inválida");
            String erroMessage = new ClienteException.InvalidPasswordException().getMessage();
            return new ResponseEntity<>(erroMessage, HttpStatus.BAD_REQUEST);
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(clienteBody.getSenha());
        clienteBody.setSenha(encodedPassword);
        double rendaMensal = clienteBody.getRendaMensal();
        if(rendaMensal < 0){
            System.out.println("Renda mensal inválida");
            String erroMessage = "Invalid Renda Mensal";
            return new ResponseEntity<>(erroMessage, HttpStatus.BAD_REQUEST);
        }
        String nome = clienteBody.getNome();
        String endereco = clienteBody.getEndereco();
        if(nome == null || endereco == null){
            System.out.println("Nome ou endereço inválidos");
            String erroMessage = "Invalid Nome or Endereço";
            return new ResponseEntity<>(erroMessage, HttpStatus.BAD_REQUEST);
        }
        Cliente cliente = repository.save(clienteBody);
        return new ResponseEntity<>(cliente, HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Cliente clienteBody){
        Optional<Cliente> cliente = repository.findByCpf(clienteBody.getCpf());
        if(cliente.isEmpty()){
            System.out.println("Cliente não encontrado");
            String erroMessage = "Cliente não encontrado";
            Map<String, String> response = Map.of("message", erroMessage);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        String senha = clienteBody.getSenha();
        if(senha == null || !SenhaUtils.senhaCorreta(senha, cliente.get().getSenha())){
            System.out.println("Cliente não encontrado");
            String erroMessage = "Cliente não encontrado";
            Map<String, String> response = Map.of("message", erroMessage);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        BobancoApplication.setClienteLogado(cliente.get());
        Map<String, String> response = Map.of("message", "Cliente logado");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    

    @PatchMapping("/update/{cpf}")
    public ResponseEntity<Object> update(@PathVariable String cpf, @RequestBody Map<String, String> requestBody) throws IllegalAccessException{
        Cpf cpfObj = new Cpf(cpf);
        Optional<Cliente> cliente = repository.findByCpf(cpfObj);
        if(cliente.isEmpty()){
            System.out.println("Cliente não encontrado");
            Map<String, String> response = Map.of("message", "Cliente não encontrado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        Cliente clienteObj = cliente.get();

        List<Field> fields = List.of(clienteObj.getClass().getDeclaredFields());

        for(Field field : fields){
            field.setAccessible(true);
            String fieldName = field.getName();
            if(requestBody.containsKey(fieldName)){
                if(fieldName.equals("cpf")){
                    System.out.println("CPF não pode ser alterado");
                    Map<String, String> response = Map.of("message", "CPF não pode ser alterado");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
                if(fieldName.equals("email")){
                    //Verifica se o email é valido
                    String email = requestBody.get(fieldName);
                    if(email == null || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")){
                        System.out.println("Email inválido");
                        Map<String, String> response = Map.of("message", "Email inválido");
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                    field.set(clienteObj, new Email(email));
                    continue;
                }
                if(fieldName.equals("senha")){
                    //Verifica se a senha é valida
                    String senha = requestBody.get(fieldName);
                    if(senha == null || !SenhaUtils.senhaValida(senha)){
                        System.out.println("Senha inválida");
                        Map<String, String> response = Map.of("message", "Senha inválida");
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    String encodedPassword = passwordEncoder.encode(senha);
                    field.set(clienteObj, encodedPassword);
                    continue;
                }
                if(fieldName.equals("rendaMensal")){
                    //Verifica se a renda mensal é valida
                    String rendaMensal = requestBody.get(fieldName);
                    if(rendaMensal == null || !rendaMensal.matches("\\d+")){
                        System.out.println("Renda mensal inválida");
                        Map<String, String> response = Map.of("message", "Renda mensal inválida");
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                    field.set(clienteObj, Double.parseDouble(rendaMensal));
                    continue;
                }
                String value = requestBody.get(fieldName);
                if(value == null){
                    System.out.println("Valor inválido");
                    Map<String, String> response = Map.of("message", "Valor inválido");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
                field.set(clienteObj, value);
            }
        }
        repository.save(clienteObj);
        Map<String, String> response = Map.of("message", "Cliente atualizado");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
