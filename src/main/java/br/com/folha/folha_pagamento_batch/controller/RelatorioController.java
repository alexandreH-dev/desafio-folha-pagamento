package br.com.folha.folha_pagamento_batch.controller;

import br.com.folha.folha_pagamento_batch.dto.RelatorioFinanceiroExecDTO;
import br.com.folha.folha_pagamento_batch.entity.RelatorioFinanceiroExec;
import br.com.folha.folha_pagamento_batch.exception.ResourceNotFoundException;
import br.com.folha.folha_pagamento_batch.repository.RelatorioFinanceiroExecRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

  private final RelatorioFinanceiroExecRepository repository;
  private final ObjectMapper objectMapper;

  @GetMapping("/{executionId}")
  public ResponseEntity<?> getRelatorio(
      @PathVariable Long executionId,
      @RequestParam(defaultValue = "TABLE") String formato) {
    List<RelatorioFinanceiroExec> relatorio = repository.findByIdExecutionId(executionId);
    if (relatorio.isEmpty()) {
      throw new ResourceNotFoundException("Relatório não encontrado para a execução com ID: " + executionId);
    }

    if ("CSV".equalsIgnoreCase(formato)) {
      String csv = gerarCsv(relatorio);
      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_" + executionId + ".csv");
      headers.add(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");
      return ResponseEntity.ok().headers(headers).body(csv);
    }

    List<RelatorioFinanceiroExecDTO> relatorioDto = relatorio.stream()
        .map(RelatorioFinanceiroExecDTO::new)
        .collect(Collectors.toList());
    return ResponseEntity.ok(relatorioDto);
  }

  @SneakyThrows
  private String gerarCsv(List<RelatorioFinanceiroExec> relatorio) {
    StringBuilder csvBuilder = new StringBuilder();
    csvBuilder.append("Matricula;Nome;CPF;Total Vantagens;Total Descontos;Liquido;Rubricas\n");

    for (RelatorioFinanceiroExec item : relatorio) {
      csvBuilder.append(item.getFuncionario().getMatricula()).append(";")
          .append(item.getFuncionario().getNome()).append(";")
          .append(item.getFuncionario().getCpf()).append(";")
          .append(item.getTotalVantagens()).append(";")
          .append(item.getTotalDescontos()).append(";")
          .append(item.getLiquido()).append(";");

      List<Map<String, Object>> detalhes = objectMapper.readValue(item.getDetalhe(), new TypeReference<>() {
      });
      StringBuilder detalhesStr = new StringBuilder("\"");
      for (Map<String, Object> detalhe : detalhes) {
        detalhesStr.append(detalhe.get("descricao")).append(" (").append(detalhe.get("codigo")).append("): ")
            .append(detalhe.get("valor")).append(" | ");
      }
      detalhesStr.append("\"");
      csvBuilder.append(detalhesStr).append("\n");
    }
    return csvBuilder.toString();
  }
}
