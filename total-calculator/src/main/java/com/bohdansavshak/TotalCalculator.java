package com.bohdansavshak;

import com.bohdansavshak.repository.redis.RedisTotalsCalculatedPerDayRepository;
import com.bohdansavshak.repository.redis.RedisTotalsCalculatedPerMonthRepository;
import com.bohdansavshak.scheduler.TotalCalculatorScheduler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@AllArgsConstructor
@Slf4j
public class TotalCalculator implements ApplicationRunner {

  private final TotalCalculatorScheduler totalCalculatorScheduler;
  private final RedisTotalsCalculatedPerDayRepository redisTotalsCalculatedPerDayRepository;
  private final RedisTotalsCalculatedPerMonthRepository redisTotalsCalculatedPerMonthRepository;

  public static void main(String[] args) {
    SpringApplication.run(TotalCalculator.class, args);
  }

  @Override
  public void run(ApplicationArguments args) {
    totalCalculatorScheduler.saveTotalsToRedis();
    redisTotalsCalculatedPerDayRepository.findAll().forEach(e -> log.info(e.toString()));
    redisTotalsCalculatedPerMonthRepository.findAll().forEach(e -> log.info(e.toString()));
  }
}
