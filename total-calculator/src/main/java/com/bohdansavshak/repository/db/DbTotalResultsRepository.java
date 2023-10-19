package com.bohdansavshak.repository.db;

import com.bohdansavshak.model.TaxiTrip;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface DbTotalResultsRepository extends CrudRepository<TaxiTrip, Long> {

    @Query(value = """
            SELECT
                EXTRACT(YEAR FROM TO_DATE(tpep_dropoff_datetime, 'MM/DD/YYYY HH:MI:SS')) AS year,
                EXTRACT(MONTH FROM TO_DATE(tpep_dropoff_datetime, 'MM/DD/YYYY HH:MI:SS')) AS month,
                EXTRACT(DAY FROM TO_DATE(tpep_dropoff_datetime, 'MM/DD/YYYY HH:MI:SS')) AS day,
                COUNT(*) AS count
            FROM
                taxi_trip
            GROUP BY
                EXTRACT(YEAR FROM TO_DATE(tpep_dropoff_datetime, 'MM/DD/YYYY HH:MI:SS')),
                EXTRACT(MONTH FROM TO_DATE(tpep_dropoff_datetime, 'MM/DD/YYYY HH:MI:SS')),
                EXTRACT(DAY FROM TO_DATE(tpep_dropoff_datetime, 'MM/DD/YYYY HH:MI:SS'))
            ORDER BY
                year, month, day;
                """, nativeQuery = true)
    List<DbTotalsCalculatedPerDay> getCalculatedResultForDays();

    @Query(value = """
            SELECT
                EXTRACT(YEAR FROM TO_DATE(tpep_dropoff_datetime, 'MM/DD/YYYY HH:MI:SS')) AS year,
                EXTRACT(MONTH FROM TO_DATE(tpep_dropoff_datetime, 'MM/DD/YYYY HH:MI:SS')) AS month,
                EXTRACT(DAY FROM TO_DATE(tpep_dropoff_datetime, 'MM/DD/YYYY HH:MI:SS')) AS day,
                COUNT(*) AS count
            FROM
                taxi_trip
            GROUP BY
                EXTRACT(YEAR FROM TO_DATE(tpep_dropoff_datetime, 'MM/DD/YYYY HH:MI:SS')),
                EXTRACT(MONTH FROM TO_DATE(tpep_dropoff_datetime, 'MM/DD/YYYY HH:MI:SS')),
                EXTRACT(DAY FROM TO_DATE(tpep_dropoff_datetime, 'MM/DD/YYYY HH:MI:SS'))
            ORDER BY
                year, month, day;
                """, nativeQuery = true)
    List<DbTotalsCalculatedPerMonth> getCalculatedResultForMonths();

    interface DbTotalsCalculatedPerDay {
        Integer getYear();
        Integer getMonth();
        Integer getDay();
        Integer getCount();
    }
    interface DbTotalsCalculatedPerMonth {
        Integer getYear();
        Integer getMonth();
        Integer getCount();
    }
}
