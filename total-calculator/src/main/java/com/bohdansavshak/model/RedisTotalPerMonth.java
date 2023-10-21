package com.bohdansavshak.model;

import com.bohdansavshak.repository.db.DbTotalRepository.TotalResult;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash("RedisTotalPerMonth")
public class RedisTotalPerMonth implements Serializable {

  @Id private LocalDate date;
  private BigDecimal total;

  public RedisTotalPerMonth() {}

  public RedisTotalPerMonth(TotalResult totalPerMonth) {
    this.date = totalPerMonth.getLocalDate();
    this.total = totalPerMonth.getTotal();
  }
}
