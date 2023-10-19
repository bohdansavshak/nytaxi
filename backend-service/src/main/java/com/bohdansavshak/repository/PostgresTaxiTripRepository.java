package com.bohdansavshak.repository;

import com.bohdansavshak.model.TaxiTrip;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PostgresTaxiTripRepository extends ReactiveCrudRepository<TaxiTrip, Long> {}
