package com.projeto.financeiro.service;

import com.projeto.financeiro.dto.mapper.TituloMapper;
import com.projeto.financeiro.dto.request.TituloRequest;
import com.projeto.financeiro.dto.response.TituloResponse;
import com.projeto.financeiro.entity.CentroDeCusto;
import com.projeto.financeiro.entity.Faturamento;
import com.projeto.financeiro.entity.Titulo;
import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.exception.BadRequestException;
import com.projeto.financeiro.exception.ConflictException;
import com.projeto.financeiro.exception.NotFoundException;
import com.projeto.financeiro.repository.CentroDeCustoRepository;
import com.projeto.financeiro.repository.FaturamentoRepository;
import com.projeto.financeiro.repository.TituloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TituloService implements CrudService<TituloRequest, TituloResponse> {

    private final TituloRepository tituloRepository;
    private final TituloMapper tituloMapper;
    private final CentroDeCustoRepository centroDeCustoRepository;
    private final FaturamentoRepository faturamentoRepository;
    private final RecorrenciaCalculator recorrenciaCalculator;

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<TituloResponse> listarTodos() {
        Usuario usuario = usuarioAutenticado();
        return tituloRepository.findAllByUsuario(usuario).stream()
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
    @Transactional
    public TituloResponse criar(TituloRequest dto) {
        validarCamposObrigatorios(dto);
        validarRegraDeRecorrencia(dto);

        Usuario usuario = usuarioAutenticado();
        List<CentroDeCusto> centrosDeCusto = buscarCentrosDeCusto(dto.centroDeCustoIds(), usuario);

        Titulo titulo = tituloMapper.toEntity(dto, usuario, centrosDeCusto);
        Titulo salvo = tituloRepository.save(titulo);

        gerarFaturamentosDoTitulo(salvo);

        return tituloMapper.toDto(tituloRepository.save(salvo));
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public TituloResponse atualizar(long id, TituloRequest dto) {
        validarCamposObrigatorios(dto);
        validarRegraDeRecorrencia(dto);

        Usuario usuario = usuarioAutenticado();
        Titulo titulo = tituloRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> tituloNaoEncontrado(id));

        if (!titulo.isAtivo()) {
            throw new ConflictException("Nao e possivel atualizar um titulo inativo.");
        }

        List<CentroDeCusto> centrosDeCusto = buscarCentrosDeCusto(dto.centroDeCustoIds(), usuario);

        removerFaturamentosNaoPagos(titulo);
        tituloMapper.updateEntity(titulo, dto, centrosDeCusto);
        Titulo atualizado = tituloRepository.save(titulo);
        gerarFaturamentosDoTitulo(atualizado);

        return tituloMapper.toDto(tituloRepository.save(atualizado));
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public void inativar(long id) {
        Usuario usuario = usuarioAutenticado();
        Titulo titulo = tituloRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> tituloNaoEncontrado(id));

        titulo.setAtivo(false);
        tituloRepository.save(titulo);
    }

    private Usuario usuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof Usuario usuario)) {
            throw new AccessDeniedException("Usuario nao autenticado");
        }
        return usuario;
    }

    private NotFoundException tituloNaoEncontrado(long id) {
        return new NotFoundException("Titulo nao encontrado com id: " + id);
    }

    private void validarCamposObrigatorios(TituloRequest dto) {
        List<String> faltantes = new ArrayList<>();

        if (dto.descricao() == null || dto.descricao().isBlank()) faltantes.add("descricao");
        if (dto.valor() == null) faltantes.add("valor");
        if (dto.valor() != null && dto.valor().compareTo(BigDecimal.ZERO) <= 0) {
            faltantes.add("valor (deve ser maior que zero)");
        }
        if (dto.dataVencimento() == null) faltantes.add("dataVencimento");
        if (dto.tipo() == null) faltantes.add("tipo");

        if (!faltantes.isEmpty()) {
            throw new BadRequestException("Campos obrigatorios ausentes ou invalidos: " + String.join(", ", faltantes));
        }
    }

    private void validarRegraDeRecorrencia(TituloRequest dto) {
        if (dto.recorrencia() == null) {
            return;
        }

        if (dto.dataFim() == null) {
            throw new BadRequestException("dataFim e obrigatoria quando recorrencia for informada.");
        }

        if (dto.dataVencimento() != null && dto.dataFim().isBefore(dto.dataVencimento())) {
            throw new BadRequestException("dataFim nao pode ser anterior a dataVencimento.");
        }
    }

    private List<CentroDeCusto> buscarCentrosDeCusto(List<Long> ids, Usuario usuario) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        List<CentroDeCusto> centros = centroDeCustoRepository.findAllById(ids);

        centros.forEach(centro -> {
            if (!centro.getUsuario().getId().equals(usuario.getId())) {
                throw new AccessDeniedException("Centro de custo nao pertence ao usuario");
            }
        });

        return centros;
    }

    private void gerarFaturamentosDoTitulo(Titulo titulo) {
        List<LocalDate> datas = recorrenciaCalculator.gerarDatas(
                titulo.getDataInicio(),
                titulo.getDataFim(),
                titulo.getRecorrencia()
        );

        for (LocalDate data : datas) {
            if (faturamentoRepository.existsByTituloAndDataVencimento(titulo, data)) {
                continue;
            }

            Faturamento faturamento = Faturamento.builder()
                    .titulo(titulo)
                    .dataVencimento(data)
                    .valor(titulo.getValor())
                    .dataPagamento(null)
                    .observacao(null)
                    .build();

            faturamentoRepository.save(faturamento);
        }
    }

    private void removerFaturamentosNaoPagos(Titulo titulo) {
        List<Faturamento> naoPagos = faturamentoRepository.findByTituloAndDataPagamentoIsNull(titulo);
        if (!naoPagos.isEmpty()) {
            faturamentoRepository.deleteAll(naoPagos);
        }
    }
}