package com.projeto.financeiro.controller;

import com.projeto.financeiro.docs.DashboardControllerDoc;
import com.projeto.financeiro.dto.response.DashboardResponse;
import com.projeto.financeiro.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor

@RequestMapping("/api/dashboard")
public class DashboardController implements DashboardControllerDoc {

    private final DashboardService service;

    @GetMapping
    public ResponseEntity<DashboardResponse> obter() {
        return ResponseEntity.ok(service.gerarDashboard());
    }
}
