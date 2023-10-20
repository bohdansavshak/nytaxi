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
public class RedisTotalPerMonth implements Serializable {

  @Id private LocalDate date;
  private BigDecimal total;

  public RedisTotalPerMonth() {}

  public RedisTotalPerMonth(DbTotalRepository.TotalResult totalPerMonth) {
    this.date = totalPerMonth.getLocalDate();
    this.total = totalPerMonth.getTotal();
  }
}
