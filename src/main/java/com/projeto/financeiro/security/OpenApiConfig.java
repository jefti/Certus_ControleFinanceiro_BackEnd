package com.projeto.financeiro.security;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Certus Controle Financeiro API", version = "v1", description = "Documentacao da API de controle financeiro"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                        .path("/api/auth/login", new PathItem().post(
                                new Operation()
                                        .addTagsItem("Autenticação")
                                        .summary("Realiza login do usuário")
                                        .description("Autentica o usuário e retorna um token JWT")
                                        .requestBody(new RequestBody()
                                                .required(true)
                                                .content(new Content().addMediaType(
                                                        "application/json",
                                                        new MediaType().schema(
                                                                new Schema<>().$ref("#/components/schemas/LoginRequest")
                                                        )
                                                ))
                                        )
                                        .responses(new ApiResponses()
                                                .addApiResponse("200", new ApiResponse()
                                                                .description("Login realizado com sucesso")
                                                                .content(new Content()
                                                                                .addMediaType(
                                                                                                "application/json",
                                                                                                new MediaType().schema(
                                                                                                                new Schema<>().$ref(
                                                                                                                                "#/components/schemas/LoginResponse")))))
                                                .addApiResponse("401", new ApiResponse()
                                                        .description("Credenciais inválidas")
                                                )
                                        )
                                )
                        );

        }

}
