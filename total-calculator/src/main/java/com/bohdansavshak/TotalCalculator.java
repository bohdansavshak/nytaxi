package com.bohdansavshak;

import com.bohdansavshak.repository.redis.RedisTotalPerDayRepository;
import com.bohdansavshak.repository.redis.RedisTotalPerMonthRepository;
import com.bohdansavshak.scheduler.TotalCalculatorScheduler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@AllArgsConstructor
@Slf4j
@EnableScheduling
public class TotalCalculator {

  public static void main(String[] args) {
    SpringApplication.run(TotalCalculator.class, args);
  }

  @Bean
  public CommandLineRunner demo(
      TotalCalculatorScheduler totalCalculatorScheduler,
      RedisTotalPerDayRepository redisTotalPerDayRepository,
      RedisTotalPerMonthRepository redisTotalPerMonthRepository) {
    return (args) -> {
      totalCalculatorScheduler.saveTotalsToRedis();
      redisTotalPerDayRepository.findAll().forEach(e -> log.info(e.toString()));
      redisTotalPerMonthRepository.findAll().forEach(e -> log.info(e.toString()));
    };
  }
}
