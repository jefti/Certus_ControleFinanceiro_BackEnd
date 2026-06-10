package com.projeto.financeiro.controller;

import com.projeto.financeiro.docs.FaturamentoControllerDoc;
import com.projeto.financeiro.dto.request.ValidarFaturamentoRequest;
import com.projeto.financeiro.dto.response.FaturamentoResponse;
import com.projeto.financeiro.service.FaturamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/faturamentos")
public class FaturamentoController implements FaturamentoControllerDoc {

    private final FaturamentoService faturamentoService;

    @GetMapping("/titulo/{tituloId}")
    public ResponseEntity<List<FaturamentoResponse>> listarPorTitulo(@PathVariable long tituloId) {
        return ResponseEntity.ok(faturamentoService.listarPorTitulo(tituloId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FaturamentoResponse> buscarPorId(@PathVariable long id) {
        return ResponseEntity.ok(faturamentoService.buscarPorId(id));
    }

    @PatchMapping("/{id}/validar")
    public ResponseEntity<FaturamentoResponse> validar(@PathVariable long id, @RequestBody(required = false) ValidarFaturamentoRequest request) {
        return ResponseEntity.ok(faturamentoService.validar(id, request));
    }
}
