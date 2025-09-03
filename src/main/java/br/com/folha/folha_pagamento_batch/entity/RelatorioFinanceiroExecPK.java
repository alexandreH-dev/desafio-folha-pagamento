package br.com.folha.folha_pagamento_batch.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RelatorioFinanceiroExecPK implements Serializable {

  private static final long serialVersionUID = 1L;

  @Column(name = "execution_id", nullable = false)
  private Long executionId;

  @Column(name = "funcionario_id", nullable = false)
  private Long funcionarioId;

  @Column(name = "competencia_ano", nullable = false)
  private Integer competenciaAno;

  @Column(name = "competencia_mes", nullable = false)
  private Integer competenciaMes;

}
