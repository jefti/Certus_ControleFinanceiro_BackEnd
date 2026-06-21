package com.projeto.financeiro.controller;

import com.projeto.financeiro.docs.UsuarioControllerDoc;
import com.projeto.financeiro.dto.request.UsuarioCreateRequest;
import com.projeto.financeiro.dto.request.UsuarioUpdateRequest;
import com.projeto.financeiro.dto.response.UsuarioResponse;
import com.projeto.financeiro.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/usuarios")
public class UsuarioController implements UsuarioControllerDoc {
    private final UsuarioService usuarioService;

    @PostMapping("/cadastrar")
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody UsuarioCreateRequest usuarioRequest) {
        UsuarioResponse usuario = usuarioService.criar(usuarioRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuario);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> obterMeuPerfil() {
        return ResponseEntity.ok(usuarioService.buscarMeuPerfil());
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioResponse> atualizar(@Valid @RequestBody UsuarioUpdateRequest usuarioRequest) {
        return ResponseEntity.ok(usuarioService.atualizar(usuarioRequest));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deletar() {
        usuarioService.inativar();
        return ResponseEntity.noContent().build();
    }
}
