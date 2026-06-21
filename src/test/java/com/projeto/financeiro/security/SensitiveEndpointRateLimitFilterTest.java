package com.projeto.financeiro.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class SensitiveEndpointRateLimitFilterTest {

    @Test
    void shouldBlockSixthForgotPasswordAttemptFromSameIp() throws Exception {
        SensitiveEndpointRateLimitFilter filter = new SensitiveEndpointRateLimitFilter();
        FilterChain chain = mock(FilterChain.class);

        for (int attempt = 1; attempt <= 5; attempt++) {
            filter.doFilter(request("/api/auth/forgot-password"), new MockHttpServletResponse(), chain);
        }

        MockHttpServletResponse blockedResponse = new MockHttpServletResponse();
        filter.doFilter(request("/api/auth/forgot-password"), blockedResponse, chain);

        assertEquals(429, blockedResponse.getStatus());
    }

    @Test
    void shouldBlockSixthRegistrationAttemptFromSameIp() throws Exception {
        SensitiveEndpointRateLimitFilter filter = new SensitiveEndpointRateLimitFilter();
        FilterChain chain = mock(FilterChain.class);

        for (int attempt = 1; attempt <= 5; attempt++) {
            filter.doFilter(request("/api/usuarios/cadastrar"), new MockHttpServletResponse(), chain);
        }

        MockHttpServletResponse blockedResponse = new MockHttpServletResponse();
        filter.doFilter(request("/api/usuarios/cadastrar"), blockedResponse, chain);

        assertEquals(429, blockedResponse.getStatus());
    }

    @Test
    void shouldKeepIndependentBudgetPerEndpoint() throws Exception {
        SensitiveEndpointRateLimitFilter filter = new SensitiveEndpointRateLimitFilter();
        FilterChain chain = mock(FilterChain.class);

        for (int attempt = 1; attempt <= 5; attempt++) {
            filter.doFilter(request("/api/auth/forgot-password"), new MockHttpServletResponse(), chain);
        }

        MockHttpServletResponse resetResponse = new MockHttpServletResponse();
        filter.doFilter(request("/api/auth/reset-password"), resetResponse, chain);

        assertEquals(200, resetResponse.getStatus());
    }

    private MockHttpServletRequest request(String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", uri);
        request.setRemoteAddr("192.0.2.20");
        return request;
    }
}