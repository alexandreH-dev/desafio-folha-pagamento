package br.com.folha.folha_pagamento_batch.repository;

import br.com.folha.folha_pagamento_batch.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long> {
  Optional<Roles> findByNome(String nome);
}
