package com.projeto.financeiro.controller;

import com.projeto.financeiro.docs.DashboardControllerDoc;
import com.projeto.financeiro.dto.response.DashboardResponse;
import com.projeto.financeiro.service.DashboardService;
import com.projeto.financeiro.service.export.DashboardExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardController implements DashboardControllerDoc {

    private final DashboardService dashboardService;
    private final DashboardExportService dashboardExportService;

    @GetMapping
    public ResponseEntity<DashboardResponse> obter(
            @RequestParam LocalDate periodoInicial,
            @RequestParam LocalDate periodoFinal
    ) {
        return ResponseEntity.ok(dashboardService.obterDashboard(periodoInicial, periodoFinal));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportar(
            @RequestParam LocalDate periodoInicial,
            @RequestParam LocalDate periodoFinal
    ) {
        byte[] arquivo = dashboardExportService.exportar(periodoInicial, periodoFinal);

        String nomeArquivo = "dashboard_%s_%s.xlsx".formatted(
                periodoInicial.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                periodoFinal.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.attachment().filename(nomeArquivo).build());

        return ResponseEntity.ok().headers(headers).body(arquivo);
    }
}
