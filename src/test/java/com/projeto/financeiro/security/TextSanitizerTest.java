package com.projeto.financeiro.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextSanitizerTest {

    private final TextSanitizer sanitizer = new TextSanitizer();

    @Test
    void shouldRemoveHtmlAndScriptFromText() {
        assertEquals("Centro seguro", sanitizer.sanitize("<script>alert(1)</script><b>Centro seguro</b>"));
    }
}
