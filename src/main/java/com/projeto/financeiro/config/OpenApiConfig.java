package com.projeto.financeiro.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Certus - API de Gerenciamento Financeiro")
                        .version("v1.0.0")
                        .description("API para gerenciamento de finanças pessoais, incluindo controle de despesas, receitas e categorias."));
    }
}

