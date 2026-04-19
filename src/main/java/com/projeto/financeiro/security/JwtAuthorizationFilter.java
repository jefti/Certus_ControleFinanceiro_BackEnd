package com.projeto.financeiro.security;

import com.projeto.financeiro.entity.Usuario;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;


public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsSecurityServer userDetailsSecurityServer;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserDetailsSecurityServer userDetailsSecurityServer) {
        super(authenticationManager);
        this.jwtUtil = jwtUtil;
        this.userDetailsSecurityServer = userDetailsSecurityServer;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            UsernamePasswordAuthenticationToken authentication = getAuthentication(header.substring(7));
            if (authentication != null && authentication.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } chain.doFilter(request,response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token){
        if(jwtUtil.isValidToken(token)){
            String username = jwtUtil.getUserName(token);
            Usuario usuario = (Usuario) userDetailsSecurityServer.loadUserByUsername(username);
            return new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
        }
        return null;
    }
}
