package com.bohdansavshak;

import static org.mockito.Mockito.*;

import com.bohdansavshak.repository.db.DbTotalRepository;
import com.bohdansavshak.repository.redis.RedisTotalPerDayRepository;
import com.bohdansavshak.repository.redis.RedisTotalPerMonthRepository;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TotalCalculatorTest {

  @Test
  void saveTotalsToRedis() {
    // SETUP
    var dbTotalRepository = mock(DbTotalRepository.class);
    var redisTotalPerDayRepository = mock(RedisTotalPerDayRepository.class);
    var redisTotalPerMonthRepository = mock(RedisTotalPerMonthRepository.class);
    var sut =
        new TotalCalculator(
            dbTotalRepository, redisTotalPerDayRepository, redisTotalPerMonthRepository);

    List<DbTotalRepository.TotalPerDay> dayTotals =
        List.of(mock(DbTotalRepository.TotalPerDay.class));
    List<DbTotalRepository.TotalPerMonth> monthTotals =
        List.of(mock(DbTotalRepository.TotalPerMonth.class));

    when(dbTotalRepository.getTotalsPerDay()).thenReturn(dayTotals);
    when(dbTotalRepository.getTotalsPerMonth()).thenReturn(monthTotals);

    // ACT
    sut.saveTotalsToRedis();

    // VERIFY
    verify(dbTotalRepository, times(1)).getTotalsPerDay();
    verify(dbTotalRepository, times(1)).getTotalsPerMonth();
    verify(redisTotalPerDayRepository, times(1)).saveAll(any(List.class));
    verify(redisTotalPerMonthRepository, times(1)).saveAll(any(List.class));
  }
}
