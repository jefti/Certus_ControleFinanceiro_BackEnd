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

import com.projeto.financeiro.dto.request.LoginRequest;
import com.projeto.financeiro.dto.response.LoginResponse;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import java.util.Map;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Certus Controle Financeiro API", version = "v1", description = "Documentacao da API de controle financeiro"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {

        @Bean
        public OpenAPI customOpenAPI() {

                Map<String, Schema> loginRequestSchemas = ModelConverters.getInstance()
                                .readAllAsResolvedSchema(new AnnotatedType(LoginRequest.class)).referencedSchemas;

                Map<String, Schema> loginResponseSchemas = ModelConverters.getInstance()
                                .readAllAsResolvedSchema(new AnnotatedType(LoginResponse.class)).referencedSchemas;

                Components components = new Components();

                loginRequestSchemas.forEach(components::addSchemas);
                loginResponseSchemas.forEach(components::addSchemas);

                return new OpenAPI()
                                .components(components)
                                .path("/api/auth/login", new PathItem().post(
                                                new Operation()
                                                                .addTagsItem("Autenticacao")
                                                                .summary("Realiza login do usuario")
                                                                .description("Autentica o usuario e retorna um token JWT")
                                                                .requestBody(new RequestBody()
                                                                                .required(true)
                                                                                .content(new Content().addMediaType(
                                                                                                "application/json",
                                                                                                new MediaType().schema(
                                                                                                                new Schema<>().$ref(
                                                                                                                                "#/components/schemas/LoginRequest")))))
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
                                                                                                .description("Credenciais inválidas")))));

        }

}
