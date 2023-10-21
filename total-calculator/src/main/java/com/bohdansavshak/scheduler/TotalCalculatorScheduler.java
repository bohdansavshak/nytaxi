package com.bohdansavshak.scheduler;

import com.bohdansavshak.model.RedisTotalPerDay;
import com.bohdansavshak.model.RedisTotalPerMonth;
import com.bohdansavshak.repository.db.DbTotalRepository;
import com.bohdansavshak.repository.redis.RedisTotalsCalculatedPerDayRepository;
import com.bohdansavshak.repository.redis.RedisTotalsCalculatedPerMonthRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class TotalCalculatorScheduler {
  private final DbTotalRepository dbTotalRepository;
  private final RedisTotalsCalculatedPerDayRepository redisTotalsCalculatedPerDayRepository;
  private final RedisTotalsCalculatedPerMonthRepository redisTotalsCalculatedPerMonthRepository;

  public void saveTotalsToRedis() {
    log.info("Scheduler started");

    var totalsPerDay = dbTotalRepository.getTotalsPerDay();
    var totalsPerMonth = dbTotalRepository.getTotalsPerMonth();

    var redisTotalsPerDay = totalsPerDay.stream().map(RedisTotalPerDay::new).toList();
    var redisTotalsPerMonth = totalsPerMonth.stream().map(RedisTotalPerMonth::new).toList();

    redisTotalsCalculatedPerDayRepository.saveAll(redisTotalsPerDay);
    redisTotalsCalculatedPerMonthRepository.saveAll(redisTotalsPerMonth);

    log.info("Scheduler finished");
  }
}
