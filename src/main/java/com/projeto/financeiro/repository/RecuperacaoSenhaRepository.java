package com.projeto.financeiro.repository;

import com.projeto.financeiro.entity.RecuperacaoSenha;
import com.projeto.financeiro.entity.Usuario;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecuperacaoSenhaRepository extends JpaRepository<RecuperacaoSenha, Long> {
    
    List<RecuperacaoSenha> findAllByUsuarioAndAtivoTrue(Usuario usuario);

    Optional<RecuperacaoSenha> findByUsuarioAndCodigoAndAtivoTrue(Usuario usuario, String codigo);

}
