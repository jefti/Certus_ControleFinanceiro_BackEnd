package com.projeto.financeiro.service;

import com.projeto.financeiro.dto.mapper.UsuarioMapper;
import com.projeto.financeiro.dto.request.UsuarioCreateRequest;
import com.projeto.financeiro.dto.request.UsuarioUpdateRequest;
import com.projeto.financeiro.dto.response.UsuarioResponse;
import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.exception.BadRequestException;
import com.projeto.financeiro.exception.ConflictException;
import com.projeto.financeiro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    public final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;


    public UsuarioResponse criar(UsuarioCreateRequest dto) {
        validateUniques(dto.email(), dto.celular(), null);
        Usuario usuario = usuarioMapper.toEntity(dto);
        validarCadastro(usuario);
        if (usuario.getDataCriacao() == null) {
            usuario.setDataCriacao(Instant.now());
        }

        Usuario salvo = usuarioRepository.save(usuario);
        return usuarioMapper.toDto(salvo);
    }

    public UsuarioResponse buscarMeuPerfil() {
        return usuarioMapper.toDto(usuarioAutenticado());
    }

    public UsuarioResponse atualizar(UsuarioUpdateRequest dto) {
        Usuario logado = usuarioAutenticado();
        Long id = logado.getId();
        validateUniques(dto.email(), dto.celular(), id);
        usuarioMapper.updateEntity(logado, dto);
        validarCadastro(logado);
        return usuarioMapper.toDto(usuarioRepository.save(logado));
    }

    public void inativar() {
        Usuario logado = usuarioAutenticado();
        logado.setDataInativacao(Instant.now());
        usuarioRepository.save(logado);
    }

    private Usuario usuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof Usuario usuario)) {
            throw new AccessDeniedException("Usuario nao autenticado");
        }
        return usuario;
    }

    private void validarCadastro(Usuario usuario) {
        List<String> faltantes = new ArrayList<>();
        if (usuario.getNome() == null || usuario.getNome().isBlank()) faltantes.add("nome");
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) faltantes.add("email");
        if (usuario.getSenha() == null || usuario.getSenha().isBlank()) faltantes.add("senha");
        if (usuario.getCelular() == null || usuario.getCelular().isBlank()) faltantes.add("celular");
        if (!faltantes.isEmpty()) {
            throw new BadRequestException("Campos obrigatórios ausentes: " + String.join(", ", faltantes));
        }
    }

    private void validateUniques(String email, String celular, Long id) {
        Optional<Usuario> usuarioExistenteEmail = usuarioRepository.findByEmail(email);
        if (usuarioExistenteEmail.isPresent()) {
            if (id == null || !usuarioExistenteEmail.get().getId().equals(id)) {
                throw new ConflictException("Usuário com email " + email + " já existe.");
            }
        }

        Optional<Usuario> usuarioExistenteCelular = usuarioRepository.findByCelular(celular);
        if (usuarioExistenteCelular.isPresent()) {
            if (id == null || !usuarioExistenteCelular.get().getId().equals(id)) {
                throw new ConflictException("Celular " + celular + " já existe na nossa base de dados.");
            }
        }
    }

}
