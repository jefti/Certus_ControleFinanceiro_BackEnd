package com.projeto.financeiro.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.financeiro.dto.request.LoginRequest;
import com.projeto.financeiro.dto.response.LoginResponse;
import com.projeto.financeiro.dto.response.UsuarioResponse;
import com.projeto.financeiro.entity.Usuario;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Locale;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        super();
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        setAuthenticationManager(authenticationManager);
        setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (request.getContentLengthLong() == 0) {
                throw new BadCredentialsException("Credenciais invÃ¡lidas");
            }

            LoginRequest login = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
            String email = login.email() == null ? "" : login.email().trim().toLowerCase(Locale.ROOT);
            String senha = login.senha() == null ? "" : login.senha();

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(email, senha);
            authToken.setDetails(authenticationDetailsSource.buildDetails(request));

            return authenticationManager.authenticate(authToken);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Credenciais invÃ¡lidas");
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult
    ) throws IOException {
        SecurityContextHolder.getContext().setAuthentication(authResult);

        Usuario usuario = (Usuario) authResult.getPrincipal();
        String token = jwtUtil.gerarToken(authResult);

        UsuarioResponse usuarioResponse = new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCelular(),
                usuario.getDataCriacao(),
                usuario.getDataInativacao()
        );

        LoginResponse loginResponse = new LoginResponse("Bearer " + token, usuarioResponse);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(loginResponse));
        response.getWriter().flush();
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed
    ) throws IOException {
        SecurityContextHolder.clearContext();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString("Falha na autenticaÃ§Ã£o: " + failed.getMessage()));
        response.getWriter().flush();
    }
}
