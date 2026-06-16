package com.projeto.financeiro.service;

import com.projeto.financeiro.dto.mapper.UsuarioMapper;
import com.projeto.financeiro.dto.response.UsuarioResponse;
import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.entity.enums.Role;
import com.projeto.financeiro.exception.BadRequestException;
import com.projeto.financeiro.exception.NotFoundException;
import com.projeto.financeiro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AdminUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public List<UsuarioResponse> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toDto)
                .toList();
    }

    public UsuarioResponse buscarPorId(long id) {
        return usuarioMapper.toDto(buscar(id));
    }

    public void desativar(long id) {
        Usuario usuario = buscar(id);
        if (usuario.getRole() == Role.ADMIN) {
            throw new BadRequestException("Contas ADMIN nao podem ser desativadas por esta operacao.");
        }
        if (usuario.getDataInativacao() == null) {
            usuario.setDataInativacao(Instant.now());
            usuarioRepository.save(usuario);
        }
    }

    public void reativar(long id) {
        Usuario usuario = buscar(id);
        if (usuario.getDataInativacao() != null) {
            usuario.setDataInativacao(null);
            usuarioRepository.save(usuario);
        }
    }

    private Usuario buscar(long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado com id: " + id));
    }
}
