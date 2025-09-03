package br.com.folha.folha_pagamento_batch.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@EqualsAndHashCode
public class UsuariosRolesPK implements Serializable {

  private static final long serialVersionUID = 1L;

  @Column(name = "usuario_id", nullable = false)
  private Long usuarioId;

  @Column(name = "role_id", nullable = false)
  private Long roleId;
}
