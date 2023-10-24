package com.bohdansavshak.respository;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.bohdansavshak.config.utils.ObjectMapperUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

  private final ReactiveRedisOperations<String, Object> redisOperations;

  @Value("${redis.total.per.day.prefix}")
  private String redisTotalPerDayPrefix;

  @Value("${redis.total.per.month.prefix}")
  private String redisTotalPerMonthPrefix;

  @Value("${redis.hashkey.total}")
  private String redisHashKeyTotal;

  public Mono<BigDecimal> getTotalPerDay(LocalDate date) {
    return getTotalFromRedisFor(redisTotalPerDayPrefix + date);
  }

  public Mono<BigDecimal> getTotalPerMonth(LocalDate date) {
    return getTotalFromRedisFor(redisTotalPerMonthPrefix + date);
  }

  private Mono<BigDecimal> getTotalFromRedisFor(String key) {
    return redisOperations
        .opsForHash()
        .get(key, redisHashKeyTotal)
        .map(total -> ObjectMapperUtils.objectMapper(total, BigDecimal.class));
  }
}
