package com.projeto.financeiro.controller;

import com.projeto.financeiro.docs.CentroDeCustoControllerDoc;
import com.projeto.financeiro.dto.request.CentroDeCustoRequest;
import com.projeto.financeiro.dto.response.CentroDeCustoResponse;
import com.projeto.financeiro.service.CentroDeCustoService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/centros-de-custo")
public class CentroDeCustoController implements CentroDeCustoControllerDoc {

    private final CentroDeCustoService centroDeCustoService;

    @PostMapping("/cadastrar")
    public ResponseEntity<CentroDeCustoResponse> cadastrar(@Valid @RequestBody CentroDeCustoRequest centroDeCustoRequest) {
        CentroDeCustoResponse centroDeCusto = centroDeCustoService.criar(centroDeCustoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(centroDeCusto);
    }

    @GetMapping("/obter")
    public ResponseEntity<List<CentroDeCustoResponse>> obterTodos() {
        return ResponseEntity.ok(centroDeCustoService.listarTodos());
    }

    @GetMapping("/obter/{id}")
    public ResponseEntity<CentroDeCustoResponse> obterPorId(@PathVariable long id) {
        return ResponseEntity.ok(centroDeCustoService.buscarPorId(id));
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<CentroDeCustoResponse> atualizar(@PathVariable long id, @Valid @RequestBody CentroDeCustoRequest centroDeCustoRequest) {
        return ResponseEntity.ok(centroDeCustoService.atualizar(id, centroDeCustoRequest));
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletar(@PathVariable long id) {
        centroDeCustoService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
