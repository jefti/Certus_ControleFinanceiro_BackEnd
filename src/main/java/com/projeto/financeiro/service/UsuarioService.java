package com.projeto.financeiro.service;

import com.projeto.financeiro.dto.mapper.UsuarioMapper;
import com.projeto.financeiro.dto.request.UsuarioRequest;
import com.projeto.financeiro.dto.response.UsuarioResponse;
import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.exception.BadRequestException;
import com.projeto.financeiro.exception.ConflictException;
import com.projeto.financeiro.exception.NotFoundException;
import com.projeto.financeiro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService implements CrudService<UsuarioRequest, UsuarioResponse> {

    public final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;


    @Override
    public UsuarioResponse criar(UsuarioRequest dto) {
        validateUniques(dto, null);
        Usuario usuario = usuarioMapper.toEntity(dto);
        validarCadastro(usuario);
        if (usuario.getDataCriacao() == null) {
            usuario.setDataCriacao(Instant.now());
        }

        Usuario salvo = usuarioRepository.save(usuario);
        return usuarioMapper.toDto(salvo);
    }

    @Override
    public List<UsuarioResponse> listarTodos() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .map(usuarioMapper::toDto)
                .toList();
    }

    @Override
    public UsuarioResponse buscarPorId(long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isEmpty()) {
            throw usuarioNaoEncontrado(id);
        }
        return usuarioMapper.toDto(usuario.get());
    }

    @Override
    public UsuarioResponse atualizar(long id, UsuarioRequest dto) {
        validateUniques(dto, id);
        return usuarioRepository.findById(id)
                .map(usuarioExistente -> {
                    usuarioMapper.updateEntity(usuarioExistente, dto);
                    validarCadastro(usuarioExistente);
                    Usuario atualizado = usuarioRepository.save(usuarioExistente);
                    return usuarioMapper.toDto(atualizado);
                })
                .orElseThrow(() -> usuarioNaoEncontrado(id));

    }

    @Override
    public void inativar(long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> usuarioNaoEncontrado(id));
        usuario.setDataInativacao(Instant.now());
        usuarioRepository.save(usuario);
    }

    private NotFoundException usuarioNaoEncontrado(long id) {
        return new NotFoundException("Usuário não encontrado com id: " + id);
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

    private void validateUniques(UsuarioRequest dto, Long id) {
        Optional<Usuario> usuarioExistenteEmail = usuarioRepository.findByEmail(dto.email());
        if (usuarioExistenteEmail.isPresent()) {
            if (id == null || !usuarioExistenteEmail.get().getId().equals(id)) {
                throw new ConflictException("Usuário com email " + dto.email() + " já existe.");
            }
        }

        Optional<Usuario> usuarioExistenteCelular = usuarioRepository.findByCelular(dto.celular());
        if (usuarioExistenteCelular.isPresent()) {
            if (id == null || !usuarioExistenteCelular.get().getId().equals(id)) {
                throw new ConflictException("Celular " + dto.celular() + " já existe na nossa base de dados.");
            }
        }
    }

}
