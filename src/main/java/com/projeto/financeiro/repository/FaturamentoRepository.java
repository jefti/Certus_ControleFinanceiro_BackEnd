package com.projeto.financeiro.repository;

import com.projeto.financeiro.entity.Faturamento;
import com.projeto.financeiro.entity.Titulo;
import com.projeto.financeiro.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}