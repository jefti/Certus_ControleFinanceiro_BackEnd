package com.projeto.financeiro.entity;

import com.projeto.financeiro.entity.enums.StatusTitulo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "faturamento")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Faturamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_titulo", nullable = false)
    private Titulo titulo;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @Column(length = 500)
    private String observacao;

    @Transient
    public StatusTitulo getStatus() {
        if (dataPagamento != null) return StatusTitulo.PAGO;
        if (dataVencimento.isBefore(LocalDate.now())) return StatusTitulo.ATRASADO;
        return StatusTitulo.EM_ABERTO;
    }
}
