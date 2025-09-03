package br.com.folha.folha_pagamento_batch.entity;

import java.io.Serializable;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios_roles")
public class UsuariosRoles implements Serializable {

  private static final long serialVersionUID = 1L;

  @EmbeddedId
  private UsuariosRolesPK id;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("usuarioId")
  @JoinColumn(name = "usuario_id")
  private Usuarios usuario;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("roleId")
  @JoinColumn(name = "role_id")
  private Roles role;
}
