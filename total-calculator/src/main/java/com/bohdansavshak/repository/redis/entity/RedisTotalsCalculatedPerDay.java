package com.bohdansavshak.repository.redis.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@RedisHash
public class RedisTotalsCalculatedPerDay implements Serializable {

  @Id private LocalDate date;
  private BigDecimal total;
}
