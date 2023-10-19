package com.bohdansavshak.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxiTrip {

  Long id;

  private String tpepPickupDatetime;

  private String tpepDropoffDatetime;

  private Integer dropOffDay;

  private Integer dropOffMonth;

  private Integer dropOffYear;

  private Integer passengerCount;

  private BigDecimal tripDistance;

  private Integer puLocationId;

  private Integer doLocationId;

  private Boolean storeAndFwdFlag;

  private BigDecimal fareAmount;

  private BigDecimal extra;

  private BigDecimal mtaTax;

  private BigDecimal improvementSurcharge;

  private BigDecimal tipAmount;

  private BigDecimal tollsAmount;

  private BigDecimal totalAmount;

  // relations

  private Long vendorId;

  private Long rateCodeId;

  private Long paymentTypeId;
}
