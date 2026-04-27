package com.projeto.financeiro.service;

import com.projeto.financeiro.dto.mapper.TituloMapper;
import com.projeto.financeiro.dto.request.TituloRequest;
import com.projeto.financeiro.dto.response.TituloResponse;
import com.projeto.financeiro.entity.CentroDeCusto;
import com.projeto.financeiro.entity.Titulo;
import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.exception.BadRequestException;
import com.projeto.financeiro.exception.NotFoundException;
import com.projeto.financeiro.repository.CentroDeCustoRepository;
import com.projeto.financeiro.repository.TituloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TituloService implements CrudService<TituloRequest, TituloResponse> {

    private final TituloRepository tituloRepository;
    private final TituloMapper tituloMapper;
    private final CentroDeCustoRepository centroDeCustoRepository;

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<TituloResponse> listarTodos() {
        Usuario usuario = usuarioAutenticado();
        return tituloRepository.findByUsuario(usuario).stream()
                .map(tituloMapper::toDto)
                .toList();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public TituloResponse buscarPorId(long id) {
        Usuario usuario = usuarioAutenticado();
        Titulo titulo = tituloRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> tituloNaoEncontrado(id));
        return tituloMapper.toDto(titulo);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public TituloResponse criar(TituloRequest dto) {
        validarCamposObrigatorios(dto);
        Usuario usuario = usuarioAutenticado();
        List<CentroDeCusto> centrosDeCusto = buscarCentrosDeCusto(dto.centroDeCustoIds(), usuario);
        Titulo titulo = tituloMapper.toEntity(dto, usuario, centrosDeCusto);
        return tituloMapper.toDto(tituloRepository.save(titulo));
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public TituloResponse atualizar(long id, TituloRequest dto) {
        validarCamposObrigatorios(dto);
        Usuario usuario = usuarioAutenticado();
        Titulo titulo = tituloRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> tituloNaoEncontrado(id));
        List<CentroDeCusto> centrosDeCusto = buscarCentrosDeCusto(dto.centroDeCustoIds(), usuario);
        tituloMapper.updateEntity(titulo, dto, centrosDeCusto);
        return tituloMapper.toDto(tituloRepository.save(titulo));
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public void inativar(long id) {
        Usuario usuario = usuarioAutenticado();
        Titulo titulo = tituloRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> tituloNaoEncontrado(id));
        tituloRepository.delete(titulo);
    }

    private Usuario usuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof Usuario usuario)) {
            throw new AccessDeniedException("Usuário não autenticado");
        }
        return usuario;
    }

    private NotFoundException tituloNaoEncontrado(long id) {
        return new NotFoundException("Título não encontrado com id: " + id);
    }

    private void validarCamposObrigatorios(TituloRequest dto) {
        List<String> faltantes = new ArrayList<>();
        if (dto.descricao() == null || dto.descricao().isBlank()) faltantes.add("descricao");
        if (dto.valor() == null) faltantes.add("valor");
        if (dto.valor() != null && dto.valor().compareTo(BigDecimal.ZERO) <= 0) faltantes.add("valor (deve ser maior que zero)");
        if (dto.dataVencimento() == null) faltantes.add("dataVencimento");
        if (dto.tipo() == null) faltantes.add("tipo");
        if (!faltantes.isEmpty()) {
            throw new BadRequestException("Campos obrigatórios ausentes ou inválidos: " + String.join(", ", faltantes));
        }
    }

    private List<CentroDeCusto> buscarCentrosDeCusto(List<Long> ids, Usuario usuario) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<CentroDeCusto> centros = centroDeCustoRepository.findAllById(ids);
        centros.forEach(centro -> {
            if (!centro.getUsuario().getId().equals(usuario.getId())) {
                throw new AccessDeniedException("Centro de custo não pertence ao usuário");
            }
        });
        return centros;
    }
}
