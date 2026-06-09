package com.projeto.financeiro.service;

import com.projeto.financeiro.dto.response.CentroDeCustoValorResponse;
import com.projeto.financeiro.dto.response.DashboardResponse;
import com.projeto.financeiro.dto.response.FaturamentoResponse;
import com.projeto.financeiro.dto.mapper.FaturamentoMapper;
import com.projeto.financeiro.entity.Faturamento;
import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.entity.enums.TipoTitulo;
import com.projeto.financeiro.repository.FaturamentoRepository;
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

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FaturamentoRepository repository;
    private final FaturamentoMapper mapper;

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public DashboardResponse gerarDashboard() {
        Usuario usuario = usuarioAutenticado();

        BigDecimal totalAReceber = repository.somarEmAbertoPorTipo(usuario, TipoTitulo.RECEBER);
        BigDecimal totalAPagar = repository.somarEmAbertoPorTipo(usuario, TipoTitulo.PAGAR);
        BigDecimal totalRecebido = repository.somarPagosPorTipo(usuario, TipoTitulo.RECEBER);
        BigDecimal totalPago = repository.somarPagosPorTipo(usuario, TipoTitulo.PAGAR);
        BigDecimal saldo = totalRecebido.subtract(totalPago);

        long quantidadeAtrasados = repository.contarAtrasados(usuario);
        BigDecimal valorAtrasados = repository.somarAtrasados(usuario);

        LocalDate dataLimite = LocalDate.now().plusDays(7);
        List<Faturamento> proximosVencimentos = repository.buscarProximosVencimentos(usuario, dataLimite);
        List<FaturamentoResponse> proximosVencimentosDto = proximosVencimentos.stream()
                .map(mapper::toDto)
                .toList();

        List<CentroDeCustoValorResponse> distribuicao =
                repository.somarEmAbertoPorCentroDeCusto(usuario, TipoTitulo.PAGAR);

        return new DashboardResponse(
                totalAReceber,
                totalAPagar,
                totalRecebido,
                totalPago,
                saldo,
                quantidadeAtrasados,
                valorAtrasados,
                proximosVencimentosDto,
                distribuicao
        );
    }

    private Usuario usuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof Usuario usuario)) {
            throw new AccessDeniedException("Usuario nao autenticado");
        }
        return usuario;
    }
}
