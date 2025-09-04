package br.com.folha.folha_pagamento_batch.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "funcionario_rubrica", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "funcionario_id", "rubrica_id", "competencia_ano", "competencia_mes" })
})
public class FuncionarioRubrica implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "funcionario_id", nullable = false)
  private Funcionarios funcionario;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "rubrica_id", nullable = false)
  private Rubricas rubrica;

  @Column(name = "competencia_ano", nullable = false)
  private Integer competenciaAno;

  @Column(name = "competencia_mes", nullable = false)
  private Integer competenciaMes;

  @Column(name = "valor", precision = 14, scale = 2, nullable = false)
  private BigDecimal valor;

  @Column(name = "status", length = 20)
  private String status;

}
