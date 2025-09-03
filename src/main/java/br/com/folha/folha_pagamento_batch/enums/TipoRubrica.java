package br.com.folha.folha_pagamento_batch.enums;

import lombok.Getter;

@Getter
public enum TipoRubrica {
  VANTAGEM('V'),
  DESCONTO('D');

  private final char codigo;

  TipoRubrica(char codigo) {
    this.codigo = codigo;
  }

  public static TipoRubrica fromCodigo(Character codigo) {
    if (codigo == null) {
      return null;
    }
    for (TipoRubrica tipo : TipoRubrica.values()) {
      if (Character.toUpperCase(codigo) == tipo.getCodigo()) {
        return tipo;
      }
    }
    throw new IllegalArgumentException("Código de tipo de rubrica inválido: " + codigo);
  }
}
