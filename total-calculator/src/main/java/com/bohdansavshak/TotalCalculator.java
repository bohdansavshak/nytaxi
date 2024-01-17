package com.bohdansavshak;

import com.bohdansavshak.model.RedisTotalPerDay;
import com.bohdansavshak.model.RedisTotalPerMonth;
import com.bohdansavshak.repository.db.DbTotalRepository;
import com.bohdansavshak.repository.redis.RedisTotalPerDayRepository;
import com.bohdansavshak.repository.redis.RedisTotalPerMonthRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

import static com.bohdansavshak.repository.db.DbTotalRepository.*;

@SpringBootApplication
@AllArgsConstructor
@Slf4j
@EnableScheduling
public class TotalCalculator implements CommandLineRunner {

  private final DbTotalRepository dbTotalRepository;
  private final RedisTotalPerDayRepository redisTotalPerDayRepository;
  private final RedisTotalPerMonthRepository redisTotalPerMonthRepository;

  public static void main(String[] args) {
    SpringApplication.run(TotalCalculator.class, args);
  }

  @Override
  public void run(String... args) {
    saveTotalsToRedis();
    redisTotalPerDayRepository.findAll().forEach(e -> log.info(e.toString()));
    redisTotalPerMonthRepository.findAll().forEach(e -> log.info(e.toString()));
    System.exit(0);
  }

  public void saveTotalsToRedis() {
    log.info("Start calculating totals per month and per day");

    List<TotalPerDay> totalsPerDay = dbTotalRepository.getTotalsPerDay();
    List<TotalPerMonth> totalsPerMonth = dbTotalRepository.getTotalsPerMonth();
    log.info("Finish calculating");

    List<RedisTotalPerDay> redisTotalsPerDay = totalsPerDay.stream().map(RedisTotalPerDay::new).toList();
    List<RedisTotalPerMonth> redisTotalsPerMonth = totalsPerMonth.stream().map(RedisTotalPerMonth::new).toList();

    log.info("Save calculated totals to redis");
    redisTotalPerDayRepository.saveAll(redisTotalsPerDay);
    redisTotalPerMonthRepository.saveAll(redisTotalsPerMonth);
    log.info("Finish saving totals to redis");
  }
}