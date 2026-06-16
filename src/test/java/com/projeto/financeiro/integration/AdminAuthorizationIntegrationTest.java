package com.projeto.financeiro.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.entity.enums.Role;
import com.projeto.financeiro.repository.UsuarioRepository;
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
class AdminAuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void regularUserIsForbiddenOnAdminEndpoint() throws Exception {
        String token = registrarELogar("user.authz@email.com");

        mockMvc.perform(get("/api/admin/usuarios")
                        .header("Authorization", token))
                .andExpect(status().isForbidden());
    }

    @Test
    void promotedUserGainsAdminAccessWithSameToken() throws Exception {
        String email = "admin.authz@email.com";
        String token = registrarELogar(email);

        // antes da promocao: USER -> 403
        mockMvc.perform(get("/api/admin/usuarios")
                        .header("Authorization", token))
                .andExpect(status().isForbidden());

        // promove a ADMIN diretamente no banco (mesma transacao do teste)
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();
        usuario.setRole(Role.ADMIN);
        usuarioRepository.save(usuario);

        // o MESMO token agora e admin: authorities sao recarregadas do banco a cada request
        mockMvc.perform(get("/api/admin/usuarios")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.email == '" + email + "')]").exists());
    }

    private String registrarELogar(String email) throws Exception {
        mockMvc.perform(post("/api/usuarios/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Usuario Authz",
                                  "email": "%s",
                                  "senha": "Senha@123",
                                  "celular": "85999990002"
                                }
                                """.formatted(email)))
                .andExpect(status().isCreated());

        String loginBody = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "senha": "Senha@123"
                                }
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(loginBody).get("token").asText();
    }
}
