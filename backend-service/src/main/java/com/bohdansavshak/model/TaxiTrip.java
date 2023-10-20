package com.bohdansavshak.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("taxi_trip")
public class TaxiTrip {

  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a");

  @Id
  @Column("taxi_trip_id")
  private Long id;

  @Column("tpep_pickup_datetime")
  private String tpepPickupDatetime;

  @Column("tpep_dropoff_datetime")
  private String tpepDropoffDatetime;

  @Column("dropoff_day")
  private Integer dropOffDay;

  @Column("dropoff_month")
  private Integer dropOffMonth;

  @Column("dropoff_year")
  private Integer dropOffYear;

  @Column("passenger_count")
  private Integer passengerCount;

  @Column("trip_distance")
  private BigDecimal tripDistance;

  @Column("pulocation_id")
  private Integer puLocationId;

  @Column("dolocation_id")
  private Integer doLocationId;

  @Column("store_and_fwd_flag")
  private Boolean storeAndFwdFlag;

  @Column("fare_amount")
  private BigDecimal fareAmount;

  @Column("extra")
  private BigDecimal extra;

  @Column("mta_tax")
  private BigDecimal mtaTax;

  @Column("improvement_surcharge")
  private BigDecimal improvementSurcharge;

  @Column("tip_amount")
  private BigDecimal tipAmount;

  @Column("tolls_amount")
  private BigDecimal tollsAmount;

  @Column("total_amount")
  private BigDecimal totalAmount;

  // relations

  @Column("vendor_id")
  private Long vendorId;

  @Column("rate_code_id")
  private Long rateCodeId;

  @Column("payment_type_id")
  private Long paymentTypeId;

  public void setDropOffDayMonthYear() {
    LocalDateTime dateTime = LocalDateTime.parse(this.tpepDropoffDatetime, DATE_TIME_FORMATTER);
    this.dropOffDay = dateTime.getDayOfMonth();
    this.dropOffMonth = dateTime.getMonthValue();
    this.dropOffYear = dateTime.getYear();
  }
}
