package com.projeto.financeiro.security;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Component;

@Component
public class TextSanitizer {

    private static final PolicyFactory PLAIN_TEXT_POLICY = new HtmlPolicyBuilder().toFactory();

    public String sanitize(String value) {
        return value == null ? null : PLAIN_TEXT_POLICY.sanitize(value).trim();
    }
}
