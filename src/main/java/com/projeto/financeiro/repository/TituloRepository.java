package com.projeto.financeiro.repository;

import com.projeto.financeiro.entity.Titulo;
import com.projeto.financeiro.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TituloRepository extends JpaRepository<Titulo, Long> {
    List<Titulo> findByUsuario(Usuario usuario);
    Optional<Titulo> findByIdAndUsuario(Long id, Usuario usuario);
}
