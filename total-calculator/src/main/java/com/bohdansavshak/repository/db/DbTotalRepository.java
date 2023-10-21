package com.bohdansavshak.repository.db;

import com.bohdansavshak.model.TaxiTrip;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface DbTotalRepository extends CrudRepository<TaxiTrip, Long> {

  @Query(
      value =
          """
                SELECT dropoff_year      AS year,
                       dropoff_month     AS month,
                       dropoff_day       AS day,
                       sum(total_amount) AS total
                FROM taxi_trip
                GROUP BY dropoff_year,
                         dropoff_month,
                         dropoff_day
                """,
      nativeQuery = true)
  List<TotalPerDay> getTotalsPerDay();

  @Query(
      value =
          """
                SELECT dropoff_year      AS year,
                       dropoff_month     AS month,
                       sum(total_amount) AS total
                FROM taxi_trip
                GROUP BY dropoff_year,
                         dropoff_month
                """,
      nativeQuery = true)
  List<TotalPerMonth> getTotalsPerMonth();

  interface TotalPerDay extends TotalResult {
    Integer getYear();

    Integer getMonth();

    Integer getDay();

    BigDecimal getTotal();

    @Override
    default LocalDate getLocalDate() {
      return LocalDate.of(getYear(), getMonth(), getDay());
    }
  }

  interface TotalPerMonth extends TotalResult {

    int FIRST_DAY_OF_MONTH = 1;

    Integer getYear();

    Integer getMonth();

    BigDecimal getTotal();

    @Override
    default LocalDate getLocalDate() {
      return LocalDate.of(getYear(), getMonth(), FIRST_DAY_OF_MONTH);
    }
  }

  interface TotalResult {
    LocalDate getLocalDate();
    BigDecimal getTotal();
  }
}
