package br.com.folha.folha_pagamento_batch.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Entidade que mapeia a tabela relatorio_financeiro_exec.
 * Utiliza a classe RelatorioFinanceiroExecPK como sua chave primária composta
 * (@EmbeddedId).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "relatorio_financeiro_exec")
public class RelatorioFinanceiroExec implements Serializable {

  private static final long serialVersionUID = 1L;

  @EmbeddedId
  private RelatorioFinanceiroExecPK id;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("funcionarioId")
  @JoinColumn(name = "funcionario_id")
  private Funcionarios funcionario;

  @Column(name = "total_vantagens", precision = 14, scale = 2, nullable = false)
  private BigDecimal totalVantagens;

  @Column(name = "total_descontos", precision = 14, scale = 2, nullable = false)
  private BigDecimal totalDescontos;

  @Column(name = "liquido", precision = 14, scale = 2, nullable = false)
  private BigDecimal liquido;

  @Column(name = "detalhe", nullable = false, columnDefinition = "jsonb")
  private String detalhe;
}
