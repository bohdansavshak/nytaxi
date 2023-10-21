package com.bohdansavshak.repository.redis;

import com.bohdansavshak.model.RedisTotalPerDay;
import java.time.LocalDate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisTotalsCalculatedPerDayRepository
    extends CrudRepository<RedisTotalPerDay, LocalDate> {}
