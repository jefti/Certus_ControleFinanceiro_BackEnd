package com.projeto.financeiro.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class LoginRateLimitFilterTest {

    @Test
    void shouldBlockSixthLoginAttemptFromSameIp() throws Exception {
        LoginRateLimitFilter filter = new LoginRateLimitFilter();
        FilterChain chain = mock(FilterChain.class);

        for (int attempt = 1; attempt <= 5; attempt++) {
            MockHttpServletRequest request = loginRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();
            filter.doFilter(request, response, chain);
        }

        MockHttpServletResponse blockedResponse = new MockHttpServletResponse();
        filter.doFilter(loginRequest(), blockedResponse, chain);

        assertEquals(429, blockedResponse.getStatus());
    }

    private MockHttpServletRequest loginRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");
        request.setRemoteAddr("192.0.2.10");
        return request;
    }
}
