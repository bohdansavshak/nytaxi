package com.bohdansavshak.repository.redis;

import com.bohdansavshak.repository.redis.entity.RedisTotalsCalculatedPerMonth;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface RedisTotalsCalculatedPerMonthRepository
    extends CrudRepository<RedisTotalsCalculatedPerMonth, LocalDate> {}
