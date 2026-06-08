package com.projeto.financeiro.controller;

import com.projeto.financeiro.docs.TituloControllerDoc;
import com.projeto.financeiro.dto.request.TituloRequest;
import com.projeto.financeiro.dto.response.TituloResponse;
import com.projeto.financeiro.service.TituloService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/titulos")
public class TituloController implements TituloControllerDoc {

    private final TituloService tituloService;

    @PostMapping("/cadastrar")
    public ResponseEntity<TituloResponse> cadastrar(@Valid @RequestBody TituloRequest tituloRequest) {
        TituloResponse titulo = tituloService.criar(tituloRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(titulo);
    }

    @GetMapping("/obter")
    public ResponseEntity<List<TituloResponse>> obterTodos() {
        return ResponseEntity.ok(tituloService.listarTodos());
    }

    @GetMapping("/obter/{id}")
    public ResponseEntity<TituloResponse> obterPorId(@PathVariable long id) {
        return ResponseEntity.ok(tituloService.buscarPorId(id));
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<TituloResponse> atualizar(@PathVariable long id, @Valid @RequestBody TituloRequest tituloRequest) {
        return ResponseEntity.ok(tituloService.atualizar(id, tituloRequest));
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletar(@PathVariable long id) {
        tituloService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
