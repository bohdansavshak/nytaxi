package com.bohdansavshak.scheduler;

import com.bohdansavshak.repository.db.DbTotalRepository;
import com.bohdansavshak.repository.redis.RedisTotalsCalculatedPerDayRepository;
import com.bohdansavshak.repository.redis.RedisTotalsCalculatedPerMonthRepository;
import com.bohdansavshak.repository.redis.entity.RedisTotalPerDay;
import com.bohdansavshak.repository.redis.entity.RedisTotalPerMonth;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class TotalCalculatorScheduler {
  private final DbTotalRepository dbTotalRepository;
  private final RedisTotalsCalculatedPerDayRepository redisTotalsCalculatedPerDayRepository;
  private final RedisTotalsCalculatedPerMonthRepository redisTotalsCalculatedPerMonthRepository;

  @Scheduled(fixedRate = 60, timeUnit = TimeUnit.SECONDS)
  public void runMe() {
    log.info("Started scheduler");

    var totalsPerDay = dbTotalRepository.getTotalPerDay();
    var totalsPerMonth = dbTotalRepository.getTotalPerMonth();

    var redisTotalsPerDay = totalsPerDay.stream().map(RedisTotalPerDay::new).toList();
    var redisTotalsPerMonth = totalsPerMonth.stream().map(RedisTotalPerMonth::new).toList();

    redisTotalsCalculatedPerDayRepository.saveAll(redisTotalsPerDay);
    redisTotalsCalculatedPerMonthRepository.saveAll(redisTotalsPerMonth);
    log.info("Finished scheduler");
  }
}
