package com.projeto.financeiro.repository;

import com.projeto.financeiro.entity.Faturamento;
import com.projeto.financeiro.entity.Titulo;
import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.entity.enums.TipoTitulo;
import com.projeto.financeiro.dto.response.CentroDeCustoValorResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FaturamentoRepository extends JpaRepository<Faturamento, Long> {

    @Query("""
        SELECT f FROM Faturamento f
        WHERE f.titulo.id = :tituloId
          AND f.titulo.usuario = :usuario
        ORDER BY f.dataVencimento ASC
    """)
    List<Faturamento> findByTituloIdAndUsuario(
            @Param("tituloId") Long tituloId,
            @Param("usuario") Usuario usuario
    );

    @Query("""
        SELECT f FROM Faturamento f
        WHERE f.titulo = :titulo
        ORDER BY f.dataVencimento ASC
    """)
    List<Faturamento> findByTitulo(@Param("titulo") Titulo titulo);

    @Query("""
        SELECT f FROM Faturamento f
        WHERE f.titulo = :titulo
          AND f.dataPagamento IS NULL
        ORDER BY f.dataVencimento ASC
    """)
    List<Faturamento> findByTituloAndDataPagamentoIsNull(@Param("titulo") Titulo titulo);

    Optional<Faturamento> findByTituloAndDataVencimento(Titulo titulo, LocalDate dataVencimento);

    boolean existsByTituloAndDataVencimento(Titulo titulo, LocalDate dataVencimento);

    @Query("""
        SELECT f FROM Faturamento f
        WHERE f.id = :id
          AND f.titulo.usuario = :usuario
    """)
    Optional<Faturamento> findByIdAndUsuario(
            @Param("id") Long id,
            @Param("usuario") Usuario usuario
    );

    @Query("""
        SELECT COALESCE(SUM(f.valor), 0)
        FROM Faturamento f
        WHERE f.titulo.usuario = :usuario
          AND f.titulo.tipo = :tipo
          AND f.dataPagamento IS NULL
    """)
    BigDecimal somarEmAbertoPorTipo(
            @Param("usuario") Usuario usuario,
            @Param("tipo") TipoTitulo tipo
    );

    @Query("""
        SELECT COALESCE(SUM(f.valor), 0)
        FROM Faturamento f
        WHERE f.titulo.usuario = :usuario
          AND f.titulo.tipo = :tipo
          AND f.dataPagamento IS NOT NULL
    """)
    BigDecimal somarPagosPorTipo(
            @Param("usuario") Usuario usuario,
            @Param("tipo") TipoTitulo tipo
    );

    @Query("""
        SELECT COUNT(f)
        FROM Faturamento f
        WHERE f.titulo.usuario = :usuario
          AND f.dataVencimento < CURRENT_DATE
          AND f.dataPagamento IS NULL
    """)
    long contarAtrasados(@Param("usuario") Usuario usuario);

    @Query("""
        SELECT COALESCE(SUM(f.valor), 0)
        FROM Faturamento f
        WHERE f.titulo.usuario = :usuario
          AND f.dataVencimento < CURRENT_DATE
          AND f.dataPagamento IS NULL
    """)
    BigDecimal somarAtrasados(@Param("usuario") Usuario usuario);

    @Query("""
        SELECT f FROM Faturamento f
        WHERE f.titulo.usuario = :usuario
          AND f.dataVencimento BETWEEN CURRENT_DATE AND :dataLimite
          AND f.dataPagamento IS NULL
        ORDER BY f.dataVencimento ASC
    """)
    List<Faturamento> buscarProximosVencimentos(
            @Param("usuario") Usuario usuario,
            @Param("dataLimite") LocalDate dataLimite
    );

    @Query("""
        SELECT new com.projeto.financeiro.dto.response.CentroDeCustoValorResponse(
            cc.descricao, COALESCE(SUM(f.valor), 0))
        FROM Faturamento f
        JOIN f.titulo t
        JOIN t.centroDeCusto cc
        WHERE t.usuario = :usuario
          AND t.tipo = :tipo
          AND f.dataPagamento IS NULL
        GROUP BY cc.descricao
        ORDER BY SUM(f.valor) DESC
    """)
    List<CentroDeCustoValorResponse> somarEmAbertoPorCentroDeCusto(
            @Param("usuario") Usuario usuario,
            @Param("tipo") TipoTitulo tipo
    );
}