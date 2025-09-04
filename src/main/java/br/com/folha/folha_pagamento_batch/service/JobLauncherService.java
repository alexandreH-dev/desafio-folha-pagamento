package br.com.folha.folha_pagamento_batch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class JobLauncherService {

  private final JobLauncher jobLauncher;
  private final Job relatorioJob;

  public Long launchRelatorioJob(Integer competenciaMes, Integer competenciaAno) throws Exception {
    JobParameters jobParameters = new JobParametersBuilder()
        .addLong("startTime", new Date().getTime())
        .addLong("competenciaMes", competenciaMes.longValue())
        .addLong("competenciaAno", competenciaAno.longValue())
        .toJobParameters();

    JobExecution jobExecution = jobLauncher.run(relatorioJob, jobParameters);
    Long executionId = jobExecution.getStepExecutions().iterator().next().getJobExecutionId();

    jobParameters = new JobParametersBuilder(jobParameters)
        .addLong("executionId", executionId)
        .toJobParameters();

    jobExecution = jobLauncher.run(relatorioJob, jobParameters);

    return jobExecution.getId();
  }
}
