package br.com.folha.folha_pagamento_batch.dto;

import br.com.folha.folha_pagamento_batch.entity.RelatorioFinanceiroExec;
import java.math.BigDecimal;

public record RelatorioFinanceiroExecDTO(
        Long executionId,
        Integer competenciaAno,
        Integer competenciaMes,
        BigDecimal totalVantagens,
        BigDecimal totalDescontos,
        BigDecimal liquido,
        String detalhe,
        RelatorioFuncionarioDTO funcionario) {
    public RelatorioFinanceiroExecDTO(RelatorioFinanceiroExec relatorio) {
        this(
                relatorio.getId().getExecutionId(),
                relatorio.getId().getCompetenciaAno(),
                relatorio.getId().getCompetenciaMes(),
                relatorio.getTotalVantagens(),
                relatorio.getTotalDescontos(),
                relatorio.getLiquido(),
                relatorio.getDetalhe(),
                new RelatorioFuncionarioDTO(relatorio.getFuncionario()));
    }
}
