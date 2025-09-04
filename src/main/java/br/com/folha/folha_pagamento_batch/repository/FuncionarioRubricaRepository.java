package br.com.folha.folha_pagamento_batch.repository;

import br.com.folha.folha_pagamento_batch.entity.FuncionarioRubrica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FuncionarioRubricaRepository extends JpaRepository<FuncionarioRubrica, Long> {

  List<FuncionarioRubrica> findByFuncionarioIdAndCompetenciaMesAndCompetenciaAno(Long funcionarioId, Integer mes,
      Integer ano);
}
