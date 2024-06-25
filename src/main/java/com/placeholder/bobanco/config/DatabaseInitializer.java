package com.placeholder.bobanco.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import com.placeholder.bobanco.repository.TesteRepository;
import com.placeholder.bobanco.repository.ClienteRepository;

import com.placeholder.bobanco.model.entity.Cliente;
import com.placeholder.bobanco.model.entity.Teste;
import com.placeholder.bobanco.model.value.Cpf;
import com.placeholder.bobanco.model.value.Email;

import org.slf4j.*;

import java.util.List;

@Configuration
public class DatabaseInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final TesteRepository repository;
    private final ClienteRepository clienteRepository;

    public DatabaseInitializer(TesteRepository repository, ClienteRepository clienteRepository) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
    }



    @Override
    public void run(String... args) throws Exception {
        log.info("Database initialization...");
        long count = clienteRepository.count(); // Passo 1
        if (count == 0) { // Passo 2
            clienteRepository.saveAll(clientes);
            log.info("Clientes inicializados no banco de dados.");
        } else { // Passo 3
            log.info("O banco de dados já contém registros. Inicialização de clientes ignorada.");
        }
    }

    // public void saveTeste() {
    //     Teste teste = new Teste();
    //     repository.save(teste);
    //     log.info("Teste saved: " + teste.getId());
    // }    

    public static List<Cliente> clientes = List.of(
        new Cliente(new Cpf("178.111.111-39"), "Gabriel", new Email("biel.ilha@gmail.com"), "111111","Rua dos Bobos, 0", 4500.00),
        new Cliente(new Cpf("195.111.111-06"), "Rafaela", new Email("rafinha@gmail.com"), "222222","Rua dos Bobos, 0", 5000.00),
        new Cliente(new Cpf("111.111.111-11"), "João", new Email("joao@gmail.com"), "333333","Rua dos Bobos, 1", 5000.00),
        new Cliente(new Cpf("111.111.111-22"), "Bruno", new Email("bruno@gmail.com"), "444444", "Rua dos Bobos, 2", 2000.00)
    );

}
