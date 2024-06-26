package com.placeholder.bobanco.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.placeholder.bobanco.repository.ClienteRepository;
import com.placeholder.bobanco.utils.SenhaUtils;
import com.placeholder.bobanco.BobancoApplication;
import com.placeholder.bobanco.exception.ClienteException;
import com.placeholder.bobanco.model.entity.Cliente;
import com.placeholder.bobanco.model.entity.Conta;
import com.placeholder.bobanco.model.value.Cpf;
import com.placeholder.bobanco.model.value.Email;
import com.placeholder.bobanco.model.entity.ContaCorrente;
import com.placeholder.bobanco.model.entity.ContaPagamento;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.ArrayList;
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

    @Tag(name = "get", description = "Métodos GET para obter informações sobre os clientes")
    @Operation(summary = "Obter todos os clientes", description = "Retorna uma lista com todos os clientes cadastrados")
    @ApiResponse(responseCode = "200", description = "Clientes encontrados")
    @GetMapping("/")
    public List<Cliente> all() {
        return repository.findAll();
    }

    @Tag(name = "get", description = "Métodos GET para obter informações sobre os clientes")
    @Operation(summary = "Obter um cliente pelo CPF", description = "Retorna um cliente específico a partir do CPF")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/cpf/{cpf}")
    public Cliente one(@PathVariable String cpf) {
        Cpf cpfObj = new Cpf(cpf); 
        Optional<Cliente> cliente = repository.findByCpf(cpfObj); // Store the Optional object
        return cliente.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    @Tag(name = "get", description = "Métodos GET para obter informações sobre os clientes")
    @Operation(summary = "Obter um cliente pelo email", description = "Retorna um cliente específico a partir do email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/email/{email}")
    public Cliente oneByEmail(@PathVariable String email) {
        Email emailObj = new Email(email);
        Optional<Cliente> cliente = repository.findByEmail(emailObj); // Store the Optional object
        return cliente.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    @Tag(name = "get", description = "Métodos GET para obter informações sobre os clientes")
    @Operation(summary = "Obter um cliente pelo ID", description = "Retorna um cliente específico a partir do ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/id/{id}")
    public Cliente oneById(@PathVariable UUID id) {
        Optional<Cliente> cliente = repository.findById(id); // Store the Optional object
        return cliente.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    @Tag(name = "get", description = "Métodos GET para obter informações sobre os clientes")
    @Operation(summary = "Obter o nome do cliente logado", description = "Retorna o nome do cliente logado, se houver.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente logado encontrado"),
        @ApiResponse(responseCode = "404", description = "Cliente não logado")
    })
    @GetMapping("/logado")
    public ResponseEntity<Object> logado(){
        if(BobancoApplication.getClienteLogado() == null){
            Map<String, String> response = Map.of("message", "Cliente não logado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        Cliente clienteLogado = repository.findByCpf(BobancoApplication.getClienteLogado()).get();
        Map<String, String> response = Map.of("message", "Cliente logado " + clienteLogado.getNome());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Tag(name = "get", description = "Métodos GET para obter informações sobre os clientes")
    @Operation(summary = "Desloga cliente", description = "Desloga o cliente logado, se houver.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente deslogado"),
        @ApiResponse(responseCode = "404", description = "Cliente não logado")
    })
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

    @Tag(name = "get", description = "Métodos GET para obter informações sobre os clientes")
    @Operation(summary = "Obter saldo do cliente logado", description = "Retorna o saldo do cliente logado, se houver.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Saldo encontrado"),
        @ApiResponse(responseCode = "404", description = "Cliente não logado"),
        @ApiResponse(responseCode = "404", description = "Cliente não possui conta")
    })
    @GetMapping("/saldo")
    public ResponseEntity <Object> getSaldo(){
        if(BobancoApplication.getClienteLogado() == null){
            Map<String, String> response = Map.of("message", "Cliente não logado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        Cliente clienteLogado = repository.findByCpf(BobancoApplication.getClienteLogado()).get();
        if(clienteLogado.getConta() == null){
            Map<String, String> response = Map.of("message", "Cliente não possui conta");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        double saldo = clienteLogado.getConta().getSaldo();
        Map<String, Double> response = Map.of("saldo", saldo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Tag(name = "post", description = "Métodos POST para criar novos clientes/contas")
    @Operation(summary = "Criar um novo cliente", description = "Cria um novo cliente a partir de um JSON")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cliente criado"),
        @ApiResponse(responseCode = "400", description = "Cliente já cadastrado"),
        @ApiResponse(responseCode = "400", description = "CPF inválido"),
        @ApiResponse(responseCode = "400", description = "Email inválido"),
        @ApiResponse(responseCode = "400", description = "Senha inválida"),
        @ApiResponse(responseCode = "400", description = "Renda mensal inválida"),
        @ApiResponse(responseCode = "400", description = "Nome ou endereço inválidos")
    })
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

    @Tag(name = "get", description = "Métodos GET para obter informações sobre os clientes")
    @Operation(summary = "Obter extrato do cliente logado", description = "Retorna o extrato do cliente logado, se houver.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Extrato encontrado"),
        @ApiResponse(responseCode = "404", description = "Cliente não logado"),
        @ApiResponse(responseCode = "404", description = "Cliente não possui conta")
    })
    @GetMapping("/extrato")
    public ResponseEntity<Object> extrato(){
        if(BobancoApplication.getClienteLogado() == null){
            Map<String, String> response = Map.of("message", "Cliente não logado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        Cliente clienteLogado = repository.findByCpf(BobancoApplication.getClienteLogado()).get();
        if(clienteLogado.getConta() == null){
            Map<String, String> response = Map.of("message", "Cliente não possui conta");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        ArrayList<Map<String, String>> transacoes = clienteLogado.getTransacoes();
        return new ResponseEntity<>(transacoes, HttpStatus.OK);
    }


    @Tag(name = "post", description = "Métodos POST para criar novos clientes/contas")
    @Operation(summary = "Logar cliente", description = "Loga o cliente a partir de um JSON")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente logado"),
        @ApiResponse(responseCode = "400", description = "Cliente não encontrado"),
        @ApiResponse(responseCode = "400", description = "Senha inválida")
    })
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
        BobancoApplication.setClienteLogado(cliente.get().getCpf());
        Map<String, String> response = Map.of("message", "Cliente logado");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Tag(name = "post", description = "Métodos POST para criar novos clientes/contas")
    @Operation(summary = "Criar conta para cliente", description = "Cria uma conta para o cliente a partir de um JSON")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Conta criada"),
        @ApiResponse(responseCode = "400", description = "Cliente não encontrado"),
        @ApiResponse(responseCode = "400", description = "Cliente já possui conta"),
        @ApiResponse(responseCode = "400", description = "Saldo inválido")
    })
    @PostMapping("/conta/{cpf}")
    public ResponseEntity<Object> createConta(@PathVariable String cpf, @RequestBody Map<String, String> requestBody) throws IllegalAccessException{
        Cpf cpfObj = new Cpf(cpf);
        Optional<Cliente> cliente = repository.findByCpf(cpfObj);
        if(cliente.isEmpty()){
            System.out.println("Cliente não encontrado");
            Map<String, String> response = Map.of("message", "Cliente não encontrado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        Cliente clienteObj = cliente.get();

        if(clienteObj.getConta() != null){
            System.out.println("Cliente já possui conta");
            Map<String, String> response = Map.of("message", "Cliente já possui conta");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        double saldo = 0;
        if(requestBody.containsKey("saldo")){
            String saldoStr = requestBody.get("saldo");
            
            if(saldoStr == null || Double.parseDouble(saldoStr) < 0){
                System.out.println("Saldo inválido");
                Map<String, String> response = Map.of("message", "Saldo inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            saldo = Double.parseDouble(saldoStr);
        }

        if(clienteObj.getRendaMensal() > 2500){
            double limiteChequeEspecial = 500;
            ContaCorrente conta = new ContaCorrente(saldo, clienteObj, limiteChequeEspecial);
            clienteObj.setConta(conta);
        }
        else{
            ContaCorrente conta = new ContaCorrente(saldo, clienteObj, 0);
            clienteObj.setConta(conta);
        }
        repository.save(clienteObj);
        Map<String, String> response = Map.of("message", "Conta criada");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Tag(name = "put", description = "Métodos PUT para atualizar informações dos clientes")
    @Operation(summary = "Atualizar informações do cliente", description = "Atualiza informações do cliente a partir de um JSON")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente atualizado"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
        @ApiResponse(responseCode = "400", description = "Email inválido"),
        @ApiResponse(responseCode = "400", description = "Email já cadastrado"),
        @ApiResponse(responseCode = "400", description = "Renda mensal inválida"),
        @ApiResponse(responseCode = "400", description = "Valor inválido"),
        @ApiResponse(responseCode = "400", description = "CPF não pode ser alterado"),
        @ApiResponse(responseCode = "400", description = "Senha não pode ser alterada dessa forma"),
        @ApiResponse(responseCode = "400", description = "Saldo não pode ser alterado dessa forma"),
        @ApiResponse(responseCode = "400", description = "Conta não pode ser alterada dessa forma"),
        @ApiResponse(responseCode = "400", description = "Transações não podem ser alteradas dessa forma")
    })
    @PutMapping("/update/{cpf}")
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
                    if(repository.findByEmail(new Email(email)).isPresent()){
                        System.out.println("Email já cadastrado");
                        Map<String, String> response = Map.of("message", "Email já cadastrado");
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                    field.set(clienteObj, new Email(email));
                    continue;
                }
                if(fieldName.equals("senha")){
                    System.out.println("Senha não pode ser alterada dessa forma");
                    Map<String, String> response = Map.of("message", "Senha não pode ser alterada dessa forma");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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
                if(fieldName.equals("saldo")){
                    System.out.println("Saldo não pode ser alterado dessa forma");
                    Map<String, String> response = Map.of("message", "Saldo não pode ser alterado dessa forma");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
                if(fieldName.equals("conta")){
                    System.out.println("Conta não pode ser alterada dessa forma");
                    Map<String, String> response = Map.of("message", "Conta não pode ser alterada dessa forma");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
                if(fieldName.equals("transacoes")){
                    System.out.println("Transações não podem ser alteradas dessa forma");
                    Map<String, String> response = Map.of("message", "Transações não podem ser alteradas dessa forma");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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

    @Tag(name = "patch", description = "Métodos PATCH para atualizar informações das contas")
    @Operation(summary = "Transferência entre contas", description = "Realiza uma transferência entre contas a partir de um JSON")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transferência realizada"),
        @ApiResponse(responseCode = "400", description = "Cliente não logado"),
        @ApiResponse(responseCode = "400", description = "Cliente não possui conta"),
        @ApiResponse(responseCode = "400", description = "Transferência para a mesma conta"),
        @ApiResponse(responseCode = "400", description = "Valor inválido"),
        @ApiResponse(responseCode = "400", description = "Saldo insuficiente")
    })
    @PatchMapping("/transferencia/{cpfDestino}")
    public ResponseEntity<Object> pix(@PathVariable String cpfDestino, @RequestBody Map<String, String> requestBody){
        if(BobancoApplication.getClienteLogado() == null){
            Map<String, String> response = Map.of("message", "Cliente não logado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        Cliente clienteLogado = repository.findByCpf(BobancoApplication.getClienteLogado()).get();
        if(clienteLogado.getConta() == null){
            Map<String, String> response = Map.of("message", "Cliente não possui conta");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        int aux = 0; //Variavel de controle do tipo de conta
        if(clienteLogado.getConta() instanceof ContaPagamento){
            aux = 1;
        }
        if(clienteLogado.getConta() instanceof ContaCorrente){
            aux = 2;
        }
        Cpf cpfDestinoObj = new Cpf(cpfDestino);
        if (cpfDestinoObj.equals(clienteLogado.getCpf())){
            Map<String, String> response = Map.of("message", "Transferência para a mesma conta");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        Optional<Cliente> clienteDestino = repository.findByCpf(cpfDestinoObj);
        if(clienteDestino.isEmpty()){
            Map<String, String> response = Map.of("message", "Cliente destino não encontrado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        Cliente clienteDestinoObj = clienteDestino.get();
        if(clienteDestinoObj.getConta() == null){
            Map<String, String> response = Map.of("message", "Cliente destino não possui conta");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        Conta contaDestino = clienteDestinoObj.getConta();
        String valorStr = requestBody.get("valor");

        if(valorStr == null){
            Map<String, String> response = Map.of("message", "Valor inválido");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        double valor = Double.parseDouble(valorStr);
        if (valor <= 0){
            Map<String, String> response = Map.of("message", "Valor inválido");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if(aux > 0){
            if(aux == 1){
                ContaPagamento contaPagamento = (ContaPagamento) clienteLogado.getConta();
                if(!contaPagamento.transferencia(contaDestino, valor)){
                    Map<String, String> response = Map.of("message", "Saldo insuficiente");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
            }
            if(aux == 2){
                ContaCorrente contaCorrente = (ContaCorrente) clienteLogado.getConta();
                if(!contaCorrente.transferencia(contaDestino, valor)){
                    Map<String, String> response = Map.of("message", "Saldo insuficiente");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
            }
        }
        clienteLogado.addTransacao("Transferência para " + cpfDestino.toString(), "-" + valorStr);
        clienteDestinoObj.addTransacao("Transferência de " + clienteLogado.getCpf().toString(), "+" + valorStr);
        repository.save(clienteLogado);
        repository.save(clienteDestinoObj);
        Map<String, String> response = Map.of("message", "Transferência realizada");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Tag(name = "patch", description = "Métodos PATCH para atualizar informações das contas")
    @Operation(summary = "Depósito em conta", description = "Realiza um depósito em conta a partir de um JSON")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Depósito realizado"),
        @ApiResponse(responseCode = "400", description = "Cliente não logado"),
        @ApiResponse(responseCode = "400", description = "Cliente não possui conta"),
        @ApiResponse(responseCode = "400", description = "Valor inválido")
    })
    @PatchMapping("/deposito")
    public ResponseEntity<Object> deposito(@RequestBody Map<String, String> requestBody){
        if(BobancoApplication.getClienteLogado() == null){
            Map<String, String> response = Map.of("message", "Cliente não logado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        Cliente clienteLogado = repository.findByCpf(BobancoApplication.getClienteLogado()).get();
        if(clienteLogado.getConta() == null){
            Map<String, String> response = Map.of("message", "Cliente não possui conta");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        String valorStr = requestBody.get("valor");
        if(valorStr == null){
            Map<String, String> response = Map.of("message", "Valor inválido");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        double valor = Double.parseDouble(valorStr);
        if (valor <= 0){
            Map<String, String> response = Map.of("message", "Valor inválido");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        clienteLogado.getConta().depositar(valor);
        clienteLogado.addTransacao("Depósito", "+" + valorStr);
        repository.save(clienteLogado);
        Map<String, String> response = Map.of("message", "Depósito realizado");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Tag(name = "patch", description = "Métodos PATCH para atualizar informações das contas")
    @Operation(summary = "Saque em conta", description = "Realiza um saque em conta a partir de um JSON")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Saque realizado"),
        @ApiResponse(responseCode = "400", description = "Cliente não logado"),
        @ApiResponse(responseCode = "400", description = "Cliente não possui conta"),
        @ApiResponse(responseCode = "400", description = "Valor inválido"),
        @ApiResponse(responseCode = "400", description = "Saldo insuficiente")
    })
    @PatchMapping("/saque")
    public ResponseEntity<Object> saque(@RequestBody Map<String, String> requestBody){
        if(BobancoApplication.getClienteLogado() == null){
            Map<String, String> response = Map.of("message", "Cliente não logado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        Cliente clienteLogado = repository.findByCpf(BobancoApplication.getClienteLogado()).get();
        if(clienteLogado.getConta() == null){
            Map<String, String> response = Map.of("message", "Cliente não possui conta");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        String valorStr = requestBody.get("valor");
        if(valorStr == null){
            Map<String, String> response = Map.of("message", "Valor inválido");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        double valor = Double.parseDouble(valorStr);
        if (valor <= 0){
            Map<String, String> response = Map.of("message", "Valor inválido");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        int aux = 0; //Variavel de controle do tipo de conta
        if(clienteLogado.getConta() instanceof ContaPagamento){
            aux = 1;
        }
        if(clienteLogado.getConta() instanceof ContaCorrente){
            aux = 2;
        }
        if(aux > 0){
            if(aux == 1){
                ContaPagamento contaPagamento = (ContaPagamento) clienteLogado.getConta();
                if(!contaPagamento.sacar(valor)){
                    Map<String, String> response = Map.of("message", "Saldo insuficiente");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
            }
            if(aux == 2){
                ContaCorrente contaCorrente = (ContaCorrente) clienteLogado.getConta();
                if(!contaCorrente.sacar(valor)){
                    Map<String, String> response = Map.of("message", "Saldo insuficiente");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
            }
        }
        clienteLogado.addTransacao("Saque", "-" + valorStr);
        repository.save(clienteLogado);
        Map<String, String> response = Map.of("message", "Saque realizado");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Tag(name = "patch", description = "Métodos PATCH para atualizar informações das contas")
    @Operation(summary = "Pagamento de conta", description = "Realiza um pagamento de conta a partir de um JSON")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pagamento realizado"),
        @ApiResponse(responseCode = "400", description = "Cliente não logado"),
        @ApiResponse(responseCode = "400", description = "Cliente não possui conta"),
        @ApiResponse(responseCode = "400", description = "Valor inválido"),
        @ApiResponse(responseCode = "400", description = "Saldo insuficiente")
    })
    @PatchMapping("/pagamentoconta")
    public ResponseEntity<Object> pagarConta(@RequestBody Map<String, String> requestBody){
        if(BobancoApplication.getClienteLogado() == null){
            Map<String, String> response = Map.of("message", "Cliente não logado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        Cliente clienteLogado = repository.findByCpf(BobancoApplication.getClienteLogado()).get();
        if(clienteLogado.getConta() == null){
            Map<String, String> response = Map.of("message", "Cliente não possui conta");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        int aux = 0; //Variavel de controle do tipo de conta
        if(clienteLogado.getConta() instanceof ContaPagamento){
            aux = 1;
        }
        if(clienteLogado.getConta() instanceof ContaCorrente){
            aux = 2;
        }
        String valorStr = requestBody.get("valor");
        if(valorStr == null){
            Map<String, String> response = Map.of("message", "Valor inválido");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        double valor = Double.parseDouble(valorStr);
        if (valor <= 0){
            Map<String, String> response = Map.of("message", "Valor inválido");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if(aux > 0){
            if(aux == 1){
                ContaPagamento contaPagamento = (ContaPagamento) clienteLogado.getConta();
                ContaCorrente foo = new ContaCorrente();
                if(!contaPagamento.transferencia(foo, valor)){
                    Map<String, String> response = Map.of("message", "Saldo insuficiente");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
            }
            if(aux == 2){
                ContaCorrente contaCorrente = (ContaCorrente) clienteLogado.getConta();
                ContaCorrente foo = new ContaCorrente();
                if(!contaCorrente.transferencia(foo, valor)){
                    Map<String, String> response = Map.of("message", "Saldo insuficiente");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
            }
        }
        clienteLogado.addTransacao("Pagamento de conta", "-" + valorStr);
        repository.save(clienteLogado);
        Map<String, String> response = Map.of("message", "Pagamento realizado");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Tag(name = "patch", description = "Métodos PATCH para atualizar informações das contas")
    @Operation(summary = "Atualiza a senha do cliente logado", description = "Atualiza a senha do cliente logado a partir de um JSON")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Senha atualizada"),
        @ApiResponse(responseCode = "404", description = "Cliente não logado"),
        @ApiResponse(responseCode = "400", description = "Senha inválida")
    })
    @PatchMapping("/update/senha")
    public ResponseEntity<Object> updateSenha(@RequestBody Map<String, String> requestBody){
        if(BobancoApplication.getClienteLogado() == null){
            Map<String, String> response = Map.of("message", "Cliente não logado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        Cliente clienteLogado = repository.findByCpf(BobancoApplication.getClienteLogado()).get();
        String senhaAtual = requestBody.get("senhaAtual");
        String novaSenha = requestBody.get("novaSenha");
        if(senhaAtual == null || novaSenha == null){
            Map<String, String> response = Map.of("message", "Senha inválida");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if(!SenhaUtils.senhaCorreta(senhaAtual, clienteLogado.getSenha())){
            Map<String, String> response = Map.of("message", "Senha inválida");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if(!SenhaUtils.senhaValida(novaSenha)){
            Map<String, String> response = Map.of("message", "Senha inválida");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(novaSenha);
        clienteLogado.setSenha(encodedPassword);
        repository.save(clienteLogado);
        Map<String, String> response = Map.of("message", "Senha atualizada");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Tag(name = "delete", description = "Métodos DELETE para deletar clientes/contas")
    @Operation(summary = "Deletar cliente", description = "Deleta um cliente a partir do CPF")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente deletado"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @DeleteMapping("/delete/{cpf}")
    public ResponseEntity<Object> delete(@PathVariable String cpf){
        Cpf cpfObj = new Cpf(cpf);
        Optional<Cliente> cliente = repository.findByCpf(cpfObj);
        if(cliente.isEmpty()){
            System.out.println("Cliente não encontrado");
            Map<String, String> response = Map.of("message", "Cliente não encontrado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        repository.delete(cliente.get());
        Map<String, String> response = Map.of("message", "Cliente deletado");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Tag(name = "delete", description = "Métodos DELETE para deletar clientes/contas")
    @Operation(summary = "Deletar conta", description = "Deleta a conta de um cliente a partir do CPF")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conta deletada"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
        @ApiResponse(responseCode = "400", description = "Cliente não possui conta")
    })
    @DeleteMapping("/delete/conta/{cpf}")
    public ResponseEntity<Object> deleteConta(@PathVariable String cpf){
        Cpf cpfObj = new Cpf(cpf);
        Optional<Cliente> cliente = repository.findByCpf(cpfObj);
        if(cliente.isEmpty()){
            System.out.println("Cliente não encontrado");
            Map<String, String> response = Map.of("message", "Cliente não encontrado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        Cliente clienteObj = cliente.get();
        if(clienteObj.getConta() == null){
            System.out.println("Cliente não possui conta");
            Map<String, String> response = Map.of("message", "Cliente não possui conta");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        clienteObj.setConta(null);
        repository.save(clienteObj);
        Map<String, String> response = Map.of("message", "Conta deletada");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
