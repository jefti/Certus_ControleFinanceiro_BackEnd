package com.projeto.financeiro.repository;

import com.projeto.financeiro.entity.RecuperacaoSenha;
import com.projeto.financeiro.entity.Usuario;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecuperacaoSenhaRepository extends JpaRepository<RecuperacaoSenha, Long> {

    List<RecuperacaoSenha> findAllByUsuarioAndAtivoTrue(Usuario usuario);

    // Há no máximo um pedido ativo por usuário (solicitarRecuperacao inativa os anteriores).
    @Query("SELECT r " +
            "FROM RecuperacaoSenha r " +
            "WHERE r.usuario = :usuario AND r.ativo = true")
    Optional<RecuperacaoSenha> buscarAtivaPorUsuario(@Param("usuario") Usuario usuario);

}
