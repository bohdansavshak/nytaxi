package com.bohdansavshak.repository.redis.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.bohdansavshak.repository.db.DbTotalRepository;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash
public class RedisTotalPerDay implements Serializable {

  @Id private LocalDate date;
  private BigDecimal total;

  public RedisTotalPerDay() {}

  public RedisTotalPerDay(DbTotalRepository.TotalResult totalPerDay) {
    this.date = totalPerDay.getLocalDate();
    this.total = totalPerDay.getTotal();
  }
}
