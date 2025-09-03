package br.com.folha.folha_pagamento_batch.repository;

import br.com.folha.folha_pagamento_batch.entity.Rubricas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RubricasRepository extends JpaRepository<Rubricas, Long> {
  Optional<Rubricas> findByCodigo(String codigo);
}
