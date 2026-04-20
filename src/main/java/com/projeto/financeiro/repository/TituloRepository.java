package com.projeto.financeiro.repository;

import com.projeto.financeiro.entity.Titulo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TituloRepository extends JpaRepository<Titulo, Long> {
    List<Titulo> findByUsuarioId(Long usuarioId);
}
