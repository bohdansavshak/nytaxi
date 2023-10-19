package com.bohdansavshak.repository.redis;

import com.bohdansavshak.repository.redis.entity.RedisTotalsCalculatedPerDay;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface RedisTotalsCalculatedPerDayRepository
    extends CrudRepository<RedisTotalsCalculatedPerDay, LocalDate> {}
