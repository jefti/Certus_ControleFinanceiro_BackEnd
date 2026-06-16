package com.projeto.financeiro.security;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.projeto.financeiro.dto.request.LoginRequest;
import com.projeto.financeiro.dto.response.LoginResponse;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;

import java.util.List;
import java.util.Map;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME = "bearerAuth";

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        Components components = new Components();

        registerSchemas(components, LoginRequest.class, LoginResponse.class);
        components.addSecuritySchemes(SECURITY_SCHEME, bearerJwtScheme());

        return new OpenAPI()
                .info(apiInfo())
                .externalDocs(externalDocs())
                .servers(servers())
                .tags(tags())
                .components(components)
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME))
                .path("/api/auth/login", loginPath());
    }

    private Info apiInfo() {
        return new Info()
                .title("Certus Controle Financeiro API")
                .version("v1")
                .description("""
                        API REST para **gestão financeira pessoal e corporativa**.

                        A plataforma **Certus Controle Financeiro** centraliza o controle de \
                        **títulos a pagar e a receber**, **centros de custo**, geração de \
                        **faturamentos recorrentes** e um **dashboard consolidado** com a \
                        saúde financeira do usuário.

                        ### Principais domínios
                        - **Autenticação** — login com JWT e recuperação de senha por e-mail
                        - **Usuários** — cadastro, atualização e inativação lógica
                        - **Títulos** — CRUD de títulos a pagar/receber com controle de vencimento
                        - **Recorrência** — títulos únicos, semanais, mensais e anuais
                        - **Faturamentos** — parcelas geradas a partir do título, com validação de pagamento
                        - **Centros de Custo** — categorização N:N de títulos
                        - **Dashboard** — indicadores financeiros consolidados por usuário

                        ### Autenticação
                        A maioria dos endpoints exige um token **JWT**. Faça login em \
                        `POST /api/auth/login`, copie o token retornado e clique em **Authorize** \
                        informando-o no formato `Bearer <token>`.

                        > Todo recurso é **escopado pelo usuário autenticado** (multitenant por usuário).
                        """)
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    private ExternalDocumentation externalDocs() {
        return new ExternalDocumentation()
                .description("Repositório do projeto no GitHub")
                .url("https://github.com/jefti/Certus_ControleFinanceiro_BackEnd");
    }

    private List<Server> servers() {
        return List.of(
                new Server()
                        .url("http://localhost:" + serverPort)
                        .description("Ambiente local de desenvolvimento"),
                new Server()
                        .url("https://certus-controle-financeiro-backend.onrender.com")
                        .description("Ambiente de produção (Render)"));
    }

    private List<Tag> tags() {
        return List.of(
                new Tag().name("Autenticacao").description("Login, logout e recuperação de senha"),
                new Tag().name("Usuarios").description("Cadastro e gestão da própria conta (/me)"),
                new Tag().name("Admin").description("Gestão de contas pelo administrador (papel ADMIN) — não acessa dados financeiros"),
                new Tag().name("Titulos").description("Títulos a pagar e a receber"),
                new Tag().name("Faturamentos").description("Parcelas/competências e validação de pagamento"),
                new Tag().name("Centros de Custo").description("Categorização de títulos"),
                new Tag().name("Dashboard").description("Indicadores financeiros consolidados"),
                new Tag().name("Health").description("Verificação de disponibilidade da aplicação"));
    }

    private SecurityScheme bearerJwtScheme() {
        return new SecurityScheme()
                .name(SECURITY_SCHEME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .description("Informe o token JWT obtido em /api/auth/login (formato: Bearer <token>)");
    }

    private void registerSchemas(Components components, Class<?>... types) {
        for (Class<?> type : types) {
            Map<String, Schema> schemas = ModelConverters.getInstance()
                    .readAllAsResolvedSchema(new AnnotatedType(type)).referencedSchemas;
            schemas.forEach(components::addSchemas);
        }
    }

    private PathItem loginPath() {
        return new PathItem().post(new Operation()
                .addTagsItem("Autenticacao")
                .summary("Realiza login do usuario")
                .description("Autentica o usuario e retorna um token JWT")
                .addSecurityItem(new SecurityRequirement())
                .requestBody(new RequestBody()
                        .required(true)
                        .content(new Content().addMediaType(
                                "application/json",
                                new MediaType().schema(
                                        new Schema<>().$ref("#/components/schemas/LoginRequest")))))
                .responses(new ApiResponses()
                        .addApiResponse("200", new ApiResponse()
                                .description("Login realizado com sucesso")
                                .content(new Content().addMediaType(
                                        "application/json",
                                        new MediaType().schema(
                                                new Schema<>().$ref("#/components/schemas/LoginResponse")))))
                        .addApiResponse("401", new ApiResponse()
                                .description("Credenciais inválidas"))));
    }

}