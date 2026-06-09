package com.projeto.financeiro.controller;

import com.projeto.financeiro.docs.DashboardControllerDoc;
import com.projeto.financeiro.dto.response.DashboardResponse;
import com.projeto.financeiro.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardController implements DashboardControllerDoc {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponse> obterDashboard(
            @RequestParam LocalDate periodoInicial,
            @RequestParam LocalDate periodoFinal
    ) {
        return ResponseEntity.ok(dashboardService.obterDashboard(periodoInicial, periodoFinal));
    }
}
