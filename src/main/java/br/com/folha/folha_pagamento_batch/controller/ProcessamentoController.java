package br.com.folha.folha_pagamento_batch.controller;

import br.com.folha.folha_pagamento_batch.service.JobLauncherService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/processamentos")
@RequiredArgsConstructor
public class ProcessamentoController {

  private final JobLauncherService jobLauncherService;
  private final JobExplorer jobExplorer;

  @PostMapping("/relatorios")
  public ResponseEntity<?> dispararRelatorio(
      @RequestParam Integer competenciaMes,
      @RequestParam Integer competenciaAno,
      @RequestParam(defaultValue = "TABLE") String formato) {
    try {
      Long executionId = jobLauncherService.launchRelatorioJob(competenciaMes, competenciaAno);
      return ResponseEntity.accepted().body(Map.of("message", "Job iniciado com sucesso.", "executionId", executionId));
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("Erro ao iniciar o job: " + e.getMessage());
    }
  }

  @GetMapping("/{executionId}/status")
  public ResponseEntity<?> getStatus(@PathVariable Long executionId) {
    JobExecution jobExecution = jobExplorer.getJobExecution(executionId);
    if (jobExecution == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(Map.of(
        "executionId", executionId,
        "status", jobExecution.getStatus(),
        "startTime", jobExecution.getStartTime(),
        "endTime", jobExecution.getEndTime(),
        "exitStatus", jobExecution.getExitStatus().getExitCode()));
  }
}
