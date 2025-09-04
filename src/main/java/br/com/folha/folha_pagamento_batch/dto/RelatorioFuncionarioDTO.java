package br.com.folha.folha_pagamento_batch.dto;

import br.com.folha.folha_pagamento_batch.entity.Funcionarios;

public record RelatorioFuncionarioDTO(
        Long id,
        String matricula,
        String nome) {
    public RelatorioFuncionarioDTO(Funcionarios funcionario) {
        this(funcionario.getId(), funcionario.getMatricula(), funcionario.getNome());
    }
}
