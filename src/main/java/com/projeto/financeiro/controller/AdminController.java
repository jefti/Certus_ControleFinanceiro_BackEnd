package com.projeto.financeiro.controller;

import com.projeto.financeiro.docs.AdminControllerDoc;
import com.projeto.financeiro.dto.response.UsuarioResponse;
import com.projeto.financeiro.service.AdminUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController implements AdminControllerDoc {

    private final AdminUsuarioService adminUsuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        return ResponseEntity.ok(adminUsuarioService.listarUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable long id) {
        return ResponseEntity.ok(adminUsuarioService.buscarPorId(id));
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(@PathVariable long id) {
        adminUsuarioService.desativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reativar")
    public ResponseEntity<Void> reativar(@PathVariable long id) {
        adminUsuarioService.reativar(id);
        return ResponseEntity.noContent().build();
    }
}
