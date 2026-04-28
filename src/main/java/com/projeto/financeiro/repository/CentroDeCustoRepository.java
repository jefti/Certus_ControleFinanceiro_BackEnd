package com.projeto.financeiro.repository;

import com.projeto.financeiro.entity.CentroDeCusto;
import com.projeto.financeiro.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CentroDeCustoRepository extends JpaRepository<CentroDeCusto, Long> {
    List<CentroDeCusto> findByUsuario(Usuario usuario);
    Optional<CentroDeCusto> findByIdAndUsuario(Long id, Usuario usuario);
    boolean existsByDescricaoAndUsuario(String descricao, Usuario usuario);
}
