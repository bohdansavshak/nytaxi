package com.bohdansavshak.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("taxi_trip")
public class TaxiTrip {

    @Id
    @Column("taxi_trip_id")
    private Long id;

    @Column("tpep_pickup_datetime")
    private String tpepPickupDatetime;

    @Column("tpep_dropoff_datetime")
    private String tpepDropoffDatetime;

    @Column("dropoff_day")
    private String dropOffDay;

    @Column("dropoff_month")
    private String dropOffMonth;

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

}
