package com.bohdansavshak.scheduler;

import com.bohdansavshak.model.RedisTotalPerDay;
import com.bohdansavshak.model.RedisTotalPerMonth;
import com.bohdansavshak.repository.db.DbTotalRepository;
import com.bohdansavshak.repository.redis.RedisTotalPerDayRepository;
import com.bohdansavshak.repository.redis.RedisTotalPerMonthRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@AllArgsConstructor
public class TotalCalculatorScheduler {
  private final DbTotalRepository dbTotalRepository;
  private final RedisTotalPerDayRepository redisTotalPerDayRepository;
  private final RedisTotalPerMonthRepository redisTotalPerMonthRepository;

  @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
  public void saveTotalsToRedis() {
    log.info("Scheduler started");

    var totalsPerDay = dbTotalRepository.getTotalsPerDay();
    var totalsPerMonth = dbTotalRepository.getTotalsPerMonth();

    var redisTotalsPerDay = totalsPerDay.stream().map(RedisTotalPerDay::new).toList();
    var redisTotalsPerMonth = totalsPerMonth.stream().map(RedisTotalPerMonth::new).toList();

    redisTotalPerDayRepository.saveAll(redisTotalsPerDay);
    redisTotalPerMonthRepository.saveAll(redisTotalsPerMonth);

    log.info("Scheduler finished");
  }
}
