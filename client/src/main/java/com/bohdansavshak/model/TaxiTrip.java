package com.bohdansavshak.model;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxiTrip implements Serializable {

  Long id;

  private String tpepPickupDatetime;

  private String tpepDropoffDatetime;

  private Integer dropOffDay;

  private Integer dropOffMonth;

  private Integer dropOffYear;

  private int passengerCount;

  private BigDecimal tripDistance;

  private int puLocationId;

  private int doLocationId;

  private Boolean storeAndFwdFlag;

  private BigDecimal fareAmount;

  private BigDecimal extra;

  private BigDecimal mtaTax;

  private BigDecimal improvementSurcharge;

  private BigDecimal tipAmount;

  private BigDecimal tollsAmount;

  private BigDecimal totalAmount;

  private Long vendorId;

  private Long rateCodeId;

  private Long paymentTypeId;
}
