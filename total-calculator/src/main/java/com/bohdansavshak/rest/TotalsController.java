package com.bohdansavshak.rest;

import com.bohdansavshak.repository.redis.RedisTotalsCalculatedPerDayRepository;
import com.bohdansavshak.repository.redis.entity.RedisTotalsCalculatedPerDay;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class TotalsController {

  private final RedisTotalsCalculatedPerDayRepository redisTotalsCalculatedPerDayRepository;

  @GetMapping("/totals-per-day")
  public Iterable<RedisTotalsCalculatedPerDay> getTotalsCalculatedPerDay() {
    return redisTotalsCalculatedPerDayRepository.findAll();
  }

  @PostMapping("/totals-per-day")
  public RedisTotalsCalculatedPerDay postTotalsPerDay(
      @RequestBody RedisTotalsCalculatedPerDay totalsPerDay) {
    return redisTotalsCalculatedPerDayRepository.save(totalsPerDay);
  }
}
