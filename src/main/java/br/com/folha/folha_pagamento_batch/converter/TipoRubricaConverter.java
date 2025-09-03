package br.com.folha.folha_pagamento_batch.converter;

import br.com.folha.folha_pagamento_batch.enums.TipoRubrica;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TipoRubricaConverter implements AttributeConverter<TipoRubrica, Character> {

  @Override
  public Character convertToDatabaseColumn(TipoRubrica attribute) {
    if (attribute == null) {
      return null;
    }
    return attribute.getCodigo();
  }

  @Override
  public TipoRubrica convertToEntityAttribute(Character dbData) {
    if (dbData == null) {
      return null;
    }
    return TipoRubrica.fromCodigo(dbData);
  }
}
