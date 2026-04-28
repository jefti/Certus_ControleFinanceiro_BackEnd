package com.projeto.financeiro.service;

import com.projeto.financeiro.dto.mapper.CentroDeCustoMapper;
import com.projeto.financeiro.dto.request.CentroDeCustoRequest;
import com.projeto.financeiro.dto.response.CentroDeCustoResponse;
import com.projeto.financeiro.entity.CentroDeCusto;
import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.exception.BadRequestException;
import com.projeto.financeiro.exception.ConflictException;
import com.projeto.financeiro.exception.NotFoundException;
import com.projeto.financeiro.repository.CentroDeCustoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CentroDeCustoService implements CrudService<CentroDeCustoRequest, CentroDeCustoResponse> {

    private final CentroDeCustoRepository centroDeCustoRepository;
    private final CentroDeCustoMapper centroDeCustoMapper;

    @Override
    @PreAuthorize("isAuthenticated()")
    public CentroDeCustoResponse criar(CentroDeCustoRequest dto) {
        validarCamposObrigatorios(dto);
        Usuario usuario = usuarioAutenticado();
        if (centroDeCustoRepository.existsByDescricaoAndUsuario(dto.descricao(), usuario)) {
            throw new ConflictException("Centro de custo com descrição '" + dto.descricao() + "' já existe.");
        }
        CentroDeCusto centroDeCusto = centroDeCustoMapper.toEntity(dto, usuario);
        return centroDeCustoMapper.toDto(centroDeCustoRepository.save(centroDeCusto));
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<CentroDeCustoResponse> listarTodos() {
        Usuario usuario = usuarioAutenticado();
        return centroDeCustoRepository.findByUsuario(usuario).stream()
                .map(centroDeCustoMapper::toDto)
                .toList();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public CentroDeCustoResponse buscarPorId(long id) {
        Usuario usuario = usuarioAutenticado();
        CentroDeCusto centroDeCusto = centroDeCustoRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> centroDeCustoNaoEncontrado(id));
        return centroDeCustoMapper.toDto(centroDeCusto);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public CentroDeCustoResponse atualizar(long id, CentroDeCustoRequest dto) {
        validarCamposObrigatorios(dto);
        Usuario usuario = usuarioAutenticado();
        CentroDeCusto centroDeCusto = centroDeCustoRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> centroDeCustoNaoEncontrado(id));
        centroDeCustoMapper.updateEntity(centroDeCusto, dto);
        return centroDeCustoMapper.toDto(centroDeCustoRepository.save(centroDeCusto));
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public void inativar(long id) {
        Usuario usuario = usuarioAutenticado();
        CentroDeCusto centroDeCusto = centroDeCustoRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> centroDeCustoNaoEncontrado(id));
        centroDeCustoRepository.delete(centroDeCusto);
    }

    private Usuario usuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof Usuario usuario)) {
            throw new AccessDeniedException("Usuário não autenticado");
        }
        return usuario;
    }

    private NotFoundException centroDeCustoNaoEncontrado(long id) {
        return new NotFoundException("Centro de custo não encontrado com id: " + id);
    }

    private void validarCamposObrigatorios(CentroDeCustoRequest dto) {
        List<String> faltantes = new ArrayList<>();
        if (dto.descricao() == null || dto.descricao().isBlank()) faltantes.add("descricao");
        if (!faltantes.isEmpty()) {
            throw new BadRequestException("Campos obrigatórios ausentes: " + String.join(", ", faltantes));
        }
    }
}
