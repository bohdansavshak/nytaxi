package com.bohdansavshak.model;

import com.bohdansavshak.repository.db.DbTotalRepository.TotalResult;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash("RedisTotalPerDay")
public class RedisTotalPerDay implements Serializable {

  @Id private LocalDate date;
  private BigDecimal total;

  public RedisTotalPerDay() {}

  public RedisTotalPerDay(TotalResult totalPerDay) {
    this.date = totalPerDay.getLocalDate();
    this.total = totalPerDay.getTotal();
  }
}
