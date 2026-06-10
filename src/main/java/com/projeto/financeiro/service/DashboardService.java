package com.projeto.financeiro.service;

import com.projeto.financeiro.dto.mapper.FaturamentoMapper;
import com.projeto.financeiro.dto.response.DashboardResponse;
import com.projeto.financeiro.dto.response.DashboardSerieResponse;
import com.projeto.financeiro.dto.response.FaturamentoResponse;
import com.projeto.financeiro.entity.Faturamento;
import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.entity.enums.TipoTitulo;
import com.projeto.financeiro.exception.BadRequestException;
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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FaturamentoRepository faturamentoRepository;
    private final TituloRepository tituloRepository;
    private final CentroDeCustoRepository centroDeCustoRepository;
    private final FaturamentoMapper faturamentoMapper;

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public DashboardResponse obterDashboard(LocalDate periodoInicial, LocalDate periodoFinal) {
        validarPeriodo(periodoInicial, periodoFinal);

        Usuario usuario = usuarioAutenticado();
        List<Faturamento> faturamentos = faturamentoRepository.findByPeriodoAndUsuario(
                periodoInicial,
                periodoFinal,
                usuario
        );

        BigDecimal totalReceitas = somarPorTipo(faturamentos, TipoTitulo.RECEBER);
        BigDecimal totalDespesas = somarPorTipo(faturamentos, TipoTitulo.PAGAR);
        BigDecimal saldo = totalReceitas.subtract(totalDespesas);

        List<DashboardSerieResponse> serieFluxoCaixa = construirSerieFluxoCaixa(faturamentos);
        List<FaturamentoResponse> lancamentos = faturamentos.stream()
                .map(faturamentoMapper::toDto)
                .toList();

        return new DashboardResponse(
                periodoInicial,
                periodoFinal,
                totalReceitas,
                totalDespesas,
                saldo,
                tituloRepository.findAllByUsuarioAndAtivo(usuario, true).size(),
                centroDeCustoRepository.findByUsuario(usuario).size(),
                faturamentos.size(),
                serieFluxoCaixa,
                lancamentos
        );
    }

    private void validarPeriodo(LocalDate periodoInicial, LocalDate periodoFinal) {
        if (periodoInicial == null || periodoFinal == null) {
            throw new BadRequestException("Periodo inicial e final sao obrigatorios.");
        }

        if (periodoFinal.isBefore(periodoInicial)) {
            throw new BadRequestException("Periodo final deve ser maior ou igual ao periodo inicial.");
        }
    }

    private BigDecimal somarPorTipo(List<Faturamento> faturamentos, TipoTitulo tipoTitulo) {
        return faturamentos.stream()
                .filter(faturamento -> faturamento.getTitulo().getTipo() == tipoTitulo)
                .map(Faturamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<DashboardSerieResponse> construirSerieFluxoCaixa(List<Faturamento> faturamentos) {
        Map<LocalDate, BigDecimal> receitasPorData = new TreeMap<>();
        Map<LocalDate, BigDecimal> despesasPorData = new TreeMap<>();

        for (Faturamento faturamento : faturamentos) {
            LocalDate data = faturamento.getDataVencimento();
            Map<LocalDate, BigDecimal> mapaAlvo = faturamento.getTitulo().getTipo() == TipoTitulo.RECEBER
                    ? receitasPorData
                    : despesasPorData;

            mapaAlvo.merge(data, faturamento.getValor(), BigDecimal::add);
        }

        return faturamentos.stream()
                .map(Faturamento::getDataVencimento)
                .distinct()
                .sorted()
                .map(data -> {
                    BigDecimal receitas = receitasPorData.getOrDefault(data, BigDecimal.ZERO);
                    BigDecimal despesas = despesasPorData.getOrDefault(data, BigDecimal.ZERO);
                    return new DashboardSerieResponse(data, receitas, despesas, receitas.subtract(despesas));
                })
                .toList();
    }

    private Usuario usuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof Usuario usuario)) {
            throw new AccessDeniedException("Usuario nao autenticado");
        }
        return usuario;
    }
}
