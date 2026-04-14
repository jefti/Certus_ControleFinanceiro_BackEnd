package com.projeto.financeiro.controller;

import com.projeto.financeiro.dto.request.UsuarioRequest;
import com.projeto.financeiro.dto.response.UsuarioResponse;
import com.projeto.financeiro.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    @PostMapping("/cadastrar")
    public ResponseEntity<UsuarioResponse> cadastrar(@RequestBody UsuarioRequest usuarioRequest) {
        UsuarioResponse usuario = usuarioService.criar(usuarioRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuario);
    }

    @GetMapping("/obter")
    public ResponseEntity<List<UsuarioResponse>> obterTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/obter/{id}")
    public ResponseEntity<UsuarioResponse> obterPorId(@PathVariable long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<UsuarioResponse> atualizar(@PathVariable long id, @RequestBody UsuarioRequest usuarioRequest) {
        return ResponseEntity.ok(usuarioService.atualizar(id, usuarioRequest));
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletar(@PathVariable long id) {
        usuarioService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
