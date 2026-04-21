package com.projeto.financeiro.repository;

import com.projeto.financeiro.entity.RecuperacaoSenha;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecuperacaoSenhaRepository extends JpaRepository<RecuperacaoSenha, Long> {
    
    Optional<RecuperacaoSenha> findAllByUsuarioAndAtivoTrue(long id_usuario);

}
