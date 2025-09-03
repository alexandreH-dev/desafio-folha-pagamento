package br.com.folha.folha_pagamento_batch.repository;

import br.com.folha.folha_pagamento_batch.entity.UsuariosRoles;
import br.com.folha.folha_pagamento_batch.entity.UsuariosRolesPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuariosRolesRepository extends JpaRepository<UsuariosRoles, UsuariosRolesPK> {
}
