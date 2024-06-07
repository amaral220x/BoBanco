package com.placeholder.bobanco.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import com.placeholder.bobanco.repository.TesteRepository;
import com.placeholder.bobanco.model.entity.Teste;

import org.slf4j.*;

@Configuration
public class DatabaseInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final TesteRepository repository;

    public DatabaseInitializer(TesteRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Database initialization...");
        saveTeste();
    }

    public void saveTeste() {
        Teste teste = new Teste();
        repository.save(teste);
        log.info("Teste saved: " + teste.getId());
    }


    
}
