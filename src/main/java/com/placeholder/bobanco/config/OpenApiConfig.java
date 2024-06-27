package com.placeholder.bobanco.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:8080");
        server.setDescription("Development Server");
        
        Contact contact = new Contact();
        contact.setName("Gabriel Amaral");
        contact.setEmail("gabrielva@dcc.ufrj.br");
        contact.setUrl("https://github.com/amaral220x");

        Info info = new Info();
        info.setTitle("Bobanco API");
        info.setVersion("1.0.0");
        info.setDescription("API para o banco Bobanco. Projeto final da trilha de BackEnd do Afrocódigos.");
        info.setContact(contact);

        return new OpenAPI()
            .addServersItem(server)
            .info(info);
    }
}
