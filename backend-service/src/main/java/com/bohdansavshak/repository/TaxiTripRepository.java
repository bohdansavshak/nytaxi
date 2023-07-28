package com.bohdansavshak.repository;

import com.bohdansavshak.model.TaxiTrip;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

// for sending aggregation to group by month and day.
public interface TaxiTripRepository extends ReactiveCrudRepository<TaxiTrip, Integer> {
    
}