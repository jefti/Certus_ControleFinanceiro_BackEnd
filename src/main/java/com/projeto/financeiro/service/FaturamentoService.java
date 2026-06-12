package com.projeto.financeiro.service;

import com.projeto.financeiro.dto.mapper.FaturamentoMapper;
import com.projeto.financeiro.dto.request.ValidarFaturamentoRequest;
import com.projeto.financeiro.dto.response.FaturamentoResponse;
import com.projeto.financeiro.entity.Faturamento;
import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.exception.BadRequestException;
import com.projeto.financeiro.exception.NotFoundException;
import com.projeto.financeiro.repository.FaturamentoRepository;
import com.projeto.financeiro.security.TextSanitizer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FaturamentoService {

    private final FaturamentoRepository faturamentoRepository;
    private final FaturamentoMapper faturamentoMapper;
    private final TextSanitizer textSanitizer;

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public List<FaturamentoResponse> listarPorTitulo(long tituloId) {
        Usuario usuario = usuarioAutenticado();
        return faturamentoRepository.findByTituloIdAndUsuario(tituloId, usuario).stream()
                .map(faturamentoMapper::toDto)
                .toList();
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public FaturamentoResponse buscarPorId(long id) {
        Usuario usuario = usuarioAutenticado();
        Faturamento faturamento = faturamentoRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new NotFoundException("Faturamento nao encontrado com id: " + id));
        return faturamentoMapper.toDto(faturamento);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public FaturamentoResponse validar(long id, ValidarFaturamentoRequest request) {
        Usuario usuario = usuarioAutenticado();
        Faturamento faturamento = faturamentoRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new NotFoundException("Faturamento nao encontrado com id: " + id));

        if (faturamento.getDataPagamento() != null) {
            throw new BadRequestException("Faturamento ja foi validado.");
        }

        LocalDateTime dataPagamento = request != null && request.dataPagamento() != null
                ? request.dataPagamento()
                : LocalDateTime.now();

        faturamento.setDataPagamento(dataPagamento);

        if (request != null && request.observacao() != null && !request.observacao().isBlank()) {
            faturamento.setObservacao(textSanitizer.sanitize(request.observacao()));
        }

        return faturamentoMapper.toDto(faturamentoRepository.save(faturamento));
    }

    private Usuario usuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof Usuario usuario)) {
            throw new AccessDeniedException("Usuario nao autenticado");
        }
        return usuario;
    }
}
