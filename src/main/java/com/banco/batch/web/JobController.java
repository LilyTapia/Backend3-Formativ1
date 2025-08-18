package com.banco.batch.web;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para lanzar los Jobs con parámetros.
 * Útil para evidencias y pruebas rápidas.
 */
@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobLauncher jobLauncher;
    private final Job dailyTransactionsReportJob;
    private final Job monthlyInterestJob;
    private final Job annualStatementJob;

    @PostMapping("/daily")
    public String runDaily(@RequestParam(required = false) String date) throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("run.date", date == null ? "" : date)
                .addLong("ts", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(dailyTransactionsReportJob, params);
        return "Lanzado dailyTransactionsReportJob con run.date=" + date;
    }

    @PostMapping("/monthly")
    public String runMonthly(@RequestParam String period) throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("run.period", period)
                .addLong("ts", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(monthlyInterestJob, params);
        return "Lanzado monthlyInterestJob con run.period=" + period;
    }

    @PostMapping("/annual")
    public String runAnnual(@RequestParam Integer year) throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("run.year", String.valueOf(year))
                .addLong("ts", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(annualStatementJob, params);
        return "Lanzado annualStatementJob con run.year=" + year;
    }
}
