package com.projeto.financeiro.service;

import com.projeto.financeiro.dto.mapper.TituloMapper;
import com.projeto.financeiro.dto.request.TituloRequest;
import com.projeto.financeiro.dto.response.TituloResponse;
import com.projeto.financeiro.entity.CentroDeCusto;
import com.projeto.financeiro.entity.Titulo;
import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.exception.NotFoundException;
import com.projeto.financeiro.repository.CentroDeCustoRepository;
import com.projeto.financeiro.repository.TituloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TituloService implements UserScopedCrudService<TituloRequest, TituloResponse> {

    private final TituloRepository tituloRepository;
    private final TituloMapper tituloMapper;
    private final CentroDeCustoRepository centroDeCustoRepository;
    private final AuthenticatedUserProvider userProvider;

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<TituloResponse> listarTodos() {
        Usuario usuario = userProvider.getCurrentUser();
        return tituloRepository.findByUsuario(usuario).stream()
                .map(tituloMapper::toDto)
                .toList();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public TituloResponse buscarPorId(long id) {
        Usuario usuario = userProvider.getCurrentUser();
        Titulo titulo = tituloRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> tituloNaoEncontrado(id));
        return tituloMapper.toDto(titulo);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public TituloResponse criar(TituloRequest dto) {
        Usuario usuario = userProvider.getCurrentUser();
        List<CentroDeCusto> centrosDeCusto = buscarCentrosDeCusto(dto.centroDeCustoIds(), usuario);
        Titulo titulo = tituloMapper.toEntity(dto, usuario, centrosDeCusto);
        return tituloMapper.toDto(tituloRepository.save(titulo));
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public TituloResponse atualizar(long id, TituloRequest dto) {
        Usuario usuario = userProvider.getCurrentUser();
        Titulo titulo = tituloRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> tituloNaoEncontrado(id));
        List<CentroDeCusto> centrosDeCusto = buscarCentrosDeCusto(dto.centroDeCustoIds(), usuario);
        tituloMapper.updateEntity(titulo, dto, centrosDeCusto);
        return tituloMapper.toDto(tituloRepository.save(titulo));
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public void inativar(long id) {
        Usuario usuario = userProvider.getCurrentUser();
        Titulo titulo = tituloRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> tituloNaoEncontrado(id));
        tituloRepository.delete(titulo);
    }

    private NotFoundException tituloNaoEncontrado(long id) {
        return new NotFoundException("Título não encontrado com id: " + id);
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
