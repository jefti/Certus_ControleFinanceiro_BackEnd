package com.projeto.financeiro.entity;

import com.projeto.financeiro.entity.enums.Recorrencia;
import com.projeto.financeiro.entity.enums.TipoTitulo;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "titulo")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Titulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descricao;


    @Column(nullable = false)
    private BigDecimal valor;


    @Column(nullable = false)
    private LocalDate dataVencimento;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTitulo tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "recorrencia")
    private Recorrencia recorrencia;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(nullable = false)
    @Builder.Default
    private boolean ativo = true;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToMany
    @JoinTable(
            name = "titulo_centro_custo",
            joinColumns = @JoinColumn(name = "id_titulo"),
            inverseJoinColumns = @JoinColumn(name = "id_centro_custo")
    )
    private List<CentroDeCusto> centroDeCusto;

    @OneToMany(mappedBy = "titulo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Faturamento> faturamentos = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (criadoEm == null) criadoEm = LocalDateTime.now();
        if (dataInicio == null) dataInicio = dataVencimento;
    }
}
