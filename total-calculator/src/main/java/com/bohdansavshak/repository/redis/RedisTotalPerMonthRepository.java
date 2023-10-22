package com.bohdansavshak.repository.redis;

import com.bohdansavshak.model.RedisTotalPerMonth;
import java.time.LocalDate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisTotalPerMonthRepository
    extends CrudRepository<RedisTotalPerMonth, LocalDate> {}
