package com.projeto.financeiro.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FluxoFinanceiroIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRejectProtectedEndpointWithoutJwt() throws Exception {
        mockMvc.perform(get("/api/centros-de-custo/obter"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldExecuteAuthenticatedFinancialFlow() throws Exception {
        mockMvc.perform(post("/api/usuarios/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Maria Silva",
                                  "email": "maria.integration@email.com",
                                  "senha": "123456",
                                  "celular": "85999990001"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("maria.integration@email.com"));

        String loginBody = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "maria.integration@email.com",
                                  "senha": "123456"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(loginBody).get("token").asText();

        String centroBody = mockMvc.perform(post("/api/centros-de-custo/cadastrar")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "descricao": "Moradia",
                                  "observacao": "Despesas residenciais"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descricao").value("Moradia"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode centro = objectMapper.readTree(centroBody);
        long centroId = centro.get("id").asLong();

        mockMvc.perform(post("/api/titulos/cadastrar")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "descricao": "Aluguel",
                                  "valor": 1200.00,
                                  "dataVencimento": "2026-06-10",
                                  "tipo": "PAGAR",
                                  "recorrencia": "MENSAL",
                                  "dataFim": "2026-08-10",
                                  "centroDeCustoIds": [%d]
                                }
                                """.formatted(centroId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descricao").value("Aluguel"))
                .andExpect(jsonPath("$.quantidadeFaturamentos").value(3));

        mockMvc.perform(get("/api/dashboard")
                        .header("Authorization", token)
                        .param("periodoInicial", "2026-06-01")
                        .param("periodoFinal", "2026-08-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalReceitas").value(0))
                .andExpect(jsonPath("$.totalDespesas").value(3600.0))
                .andExpect(jsonPath("$.saldo").value(-3600.0))
                .andExpect(jsonPath("$.quantidadeTitulosAtivos").value(1))
                .andExpect(jsonPath("$.quantidadeCentrosDeCusto").value(1))
                .andExpect(jsonPath("$.quantidadeLancamentos").value(3));
    }
}
