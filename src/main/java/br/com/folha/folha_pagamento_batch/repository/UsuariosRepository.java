package br.com.folha.folha_pagamento_batch.repository;

import br.com.folha.folha_pagamento_batch.entity.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuarios, Long> {
  Optional<Usuarios> findByUsername(String username);
}
