package br.com.folha.folha_pagamento_batch.repository;

import br.com.folha.folha_pagamento_batch.entity.RelatorioFinanceiroExec;
import br.com.folha.folha_pagamento_batch.entity.RelatorioFinanceiroExecPK;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelatorioFinanceiroExecRepository
                extends JpaRepository<RelatorioFinanceiroExec, RelatorioFinanceiroExecPK> {

        List<RelatorioFinanceiroExec> findByIdExecutionId(Long executionId);
}
