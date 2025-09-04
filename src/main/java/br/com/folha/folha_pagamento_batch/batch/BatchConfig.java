package br.com.folha.folha_pagamento_batch.batch;

import br.com.folha.folha_pagamento_batch.entity.FuncionarioRubrica;
import br.com.folha.folha_pagamento_batch.entity.Funcionarios;
import br.com.folha.folha_pagamento_batch.entity.RelatorioFinanceiroExec;
import br.com.folha.folha_pagamento_batch.entity.RelatorioFinanceiroExecPK;
import br.com.folha.folha_pagamento_batch.enums.TipoRubrica;
import br.com.folha.folha_pagamento_batch.repository.FuncionarioRubricaRepository;
import br.com.folha.folha_pagamento_batch.repository.FuncionariosRepository;
import br.com.folha.folha_pagamento_batch.repository.RelatorioFinanceiroExecRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final FuncionariosRepository funcionariosRepository;
  private final FuncionarioRubricaRepository funcionarioRubricaRepository;
  private final RelatorioFinanceiroExecRepository relatorioFinanceiroExecRepository;
  private final ObjectMapper objectMapper;

  @Bean
  public Job relatorioJob(Step relatorioStep) {
    return new JobBuilder("relatorioJob", jobRepository)
        .start(relatorioStep)
        .build();
  }

  @Bean
  public Step relatorioStep(RepositoryItemReader<Funcionarios> funcionarioReader,
      ItemProcessor<Funcionarios, RelatorioFinanceiroExec> relatorioProcessor,
      RepositoryItemWriter<RelatorioFinanceiroExec> relatorioWriter) {
    return new StepBuilder("relatorioStep", jobRepository)
        .<Funcionarios, RelatorioFinanceiroExec>chunk(10, transactionManager)
        .reader(funcionarioReader)
        .processor(relatorioProcessor)
        .writer(relatorioWriter)
        .build();
  }

  @Bean
  @StepScope
  public RepositoryItemReader<Funcionarios> funcionarioReader() {
    return new RepositoryItemReaderBuilder<Funcionarios>()
        .name("funcionarioReader")
        .repository(funcionariosRepository)
        .methodName("findAll")
        .sorts(new HashMap<String, Sort.Direction>() {
          {
            put("id", Sort.Direction.ASC);
          }
        })
        .build();
  }

  @Bean
  @StepScope
  public ItemProcessor<Funcionarios, RelatorioFinanceiroExec> relatorioProcessor(
      @Value("#{stepExecution}") StepExecution stepExecution,
      @Value("#{jobParameters['competenciaMes']}") Integer competenciaMes,
      @Value("#{jobParameters['competenciaAno']}") Integer competenciaAno) {
    return funcionario -> {
      Long executionId = stepExecution.getJobExecution().getId();

      List<FuncionarioRubrica> rubricasDoMes = funcionarioRubricaRepository
          .findByFuncionarioIdAndCompetenciaMesAndCompetenciaAno(
              funcionario.getId(), competenciaMes, competenciaAno);

      if (rubricasDoMes.isEmpty()) {
        return null;
      }

      BigDecimal totalVantagens = BigDecimal.ZERO;
      BigDecimal totalDescontos = BigDecimal.ZERO;

      for (FuncionarioRubrica rubrica : rubricasDoMes) {
        if (rubrica.getRubrica().getTipo() == TipoRubrica.VANTAGEM) {
          totalVantagens = totalVantagens.add(rubrica.getValor());
        } else if (rubrica.getRubrica().getTipo() == TipoRubrica.DESCONTO) {
          totalDescontos = totalDescontos.add(rubrica.getValor());
        }
      }

      BigDecimal liquido = totalVantagens.subtract(totalDescontos);
      String detalheJson;
      try {
        List<Map<String, Object>> detalhes = rubricasDoMes.stream()
            .map(fr -> {
              return Map.ofEntries(
                  Map.entry("codigo", (Object) fr.getRubrica().getCodigo()),
                  Map.entry("descricao", (Object) fr.getRubrica().getDescricao()),
                  Map.entry("valor", (Object) fr.getValor()),
                  Map.entry("tipo", (Object) fr.getRubrica().getTipo().name()));
            })
            .collect(Collectors.toList());
        detalheJson = objectMapper.writeValueAsString(detalhes);
      } catch (Exception e) {
        detalheJson = "{\"error\": \"Falha ao serializar detalhes.\"}";
      }

      RelatorioFinanceiroExec relatorio = new RelatorioFinanceiroExec();
      RelatorioFinanceiroExecPK pk = new RelatorioFinanceiroExecPK(
          executionId,
          funcionario.getId(),
          competenciaAno,
          competenciaMes);
      relatorio.setId(pk);
      relatorio.setFuncionario(funcionario);
      relatorio.setTotalVantagens(totalVantagens);
      relatorio.setTotalDescontos(totalDescontos);
      relatorio.setLiquido(liquido);
      relatorio.setDetalhe(detalheJson);

      return relatorio;
    };
  }

  @Bean
  public RepositoryItemWriter<RelatorioFinanceiroExec> relatorioWriter() {
    RepositoryItemWriter<RelatorioFinanceiroExec> writer = new RepositoryItemWriter<>();
    writer.setRepository(relatorioFinanceiroExecRepository);
    writer.setMethodName("save");
    return writer;
  }
}
