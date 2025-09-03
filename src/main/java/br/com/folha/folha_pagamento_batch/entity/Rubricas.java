package br.com.folha.folha_pagamento_batch.entity;

import java.io.Serializable;

import br.com.folha.folha_pagamento_batch.converter.TipoRubricaConverter;
import br.com.folha.folha_pagamento_batch.enums.TipoRubrica;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
@Table(name = "rubricas")
public class Rubricas implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "codigo", length = 20, nullable = false, unique = true)
  private String codigo;

  @Column(name = "descricao", length = 120, nullable = false)
  private String descricao;

  @Convert(converter = TipoRubricaConverter.class)
  @Column(name = "tipo", length = 1, nullable = false)
  private TipoRubrica tipo;

}
