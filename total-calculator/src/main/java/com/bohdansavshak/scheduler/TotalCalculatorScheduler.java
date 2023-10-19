package com.bohdansavshak.scheduler;

import com.bohdansavshak.repository.db.DbTotalResultsRepository;
import com.bohdansavshak.repository.db.DbTotalResultsRepository.DbTotalsCalculatedPerDay;
import com.bohdansavshak.repository.db.DbTotalResultsRepository.DbTotalsCalculatedPerMonth;
import com.bohdansavshak.repository.redis.RedisTotalsCalculatedPerDayRepository;
import com.bohdansavshak.repository.redis.RedisTotalsCalculatedPerMonthRepository;
import com.bohdansavshak.repository.redis.entity.RedisTotalsCalculatedPerDay;
import com.bohdansavshak.repository.redis.entity.RedisTotalsCalculatedPerMonth;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class TotalCalculatorScheduler {

    public static final int FIRST_DAY_OF_MONTH = 1;
    private final DbTotalResultsRepository dbTotalResultsRepository;
    private final RedisTotalsCalculatedPerDayRepository redisTotalsCalculatedPerDayRepository;
    private final RedisTotalsCalculatedPerMonthRepository redisTotalsCalculatedPerMonthRepository;

    @Scheduled(fixedRate = 60, timeUnit = TimeUnit.SECONDS)
    public void runMe() {
        log.info("Started scheduler");
        var resultForDays = dbTotalResultsRepository.getCalculatedResultForDays();
        var resultForMonths = dbTotalResultsRepository.getCalculatedResultForMonths();

        var redisResultsForDays = resultForDays.stream().map(TotalCalculatorScheduler::map).toList();
        var redisResultsForMonths = resultForMonths.stream().map(TotalCalculatorScheduler::map).toList();

        redisTotalsCalculatedPerDayRepository.saveAll(redisResultsForDays);
        redisTotalsCalculatedPerMonthRepository.saveAll(redisResultsForMonths);
        log.info("Finished scheduler");
    }

    private static RedisTotalsCalculatedPerMonth map(DbTotalsCalculatedPerMonth totalsPerMonth) {
        var redisEntity = new RedisTotalsCalculatedPerMonth();
        redisEntity.setDate(LocalDate.of(totalsPerMonth.getYear(), totalsPerMonth.getMonth(), FIRST_DAY_OF_MONTH));
        redisEntity.setTotal(BigDecimal.valueOf(totalsPerMonth.getCount()));
        return redisEntity;
    }

    private static RedisTotalsCalculatedPerDay map(DbTotalsCalculatedPerDay totalsPerDay) {
        var redisEntity = new RedisTotalsCalculatedPerDay();
        redisEntity.setDate(LocalDate.of(totalsPerDay.getYear(), totalsPerDay.getMonth(), totalsPerDay.getDay()));
        redisEntity.setTotal(BigDecimal.valueOf(totalsPerDay.getCount()));
        return redisEntity;
    }
}
