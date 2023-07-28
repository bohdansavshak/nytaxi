package com.bohdansavshak.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxiTrip {

    Long id;

    private String tpepPickupDatetime;

    private String tpepDropoffDatetime;

    private String dropOffDay;

    private String dropOffMonth;

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
