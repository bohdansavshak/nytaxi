package com.bohdansavshak.repository.redis;

import com.bohdansavshak.repository.redis.entity.RedisTotalPerMonth;
import java.time.LocalDate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisTotalsCalculatedPerMonthRepository
    extends CrudRepository<RedisTotalPerMonth, LocalDate> {}
