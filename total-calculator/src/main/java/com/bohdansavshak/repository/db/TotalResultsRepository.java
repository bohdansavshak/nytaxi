package com.bohdansavshak.repository.db;

import com.bohdansavshak.model.TaxiTrip;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface TotalResultsRepository extends CrudRepository<TaxiTrip, Long> {

    @Query("""
        query goes here; 
    """)
    String myQuery();
}
