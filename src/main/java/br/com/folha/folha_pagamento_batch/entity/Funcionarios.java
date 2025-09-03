package br.com.folha.folha_pagamento_batch.entity;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "funcionarios")
public class Funcionarios implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "matricula", length = 30, unique = true, nullable = false)
  private String matricula;

  @Column(name = "nome", length = 150, nullable = false)
  private String nome;

  @Column(name = "cpf", length = 14, unique = true, nullable = false)
  private String cpf;

  @Column(name = "orgao", length = 60)
  private String orgao;

  @Column(name = "lotacao", length = 120)
  private String lotacao;

  @Column(name = "dt_admissao")
  private LocalDate dtAdmissao;

}
