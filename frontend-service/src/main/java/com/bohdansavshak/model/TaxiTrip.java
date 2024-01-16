package com.bohdansavshak.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.bohdansavshak.rest.validator.DateTimeFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxiTrip {

  private static final DateTimeFormatter DATE_TIME_FORMATTER =
          DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a");

  Long id;

  @NotBlank(message = "tpepPickupDatetime datetime is mandatory.")
  private String tpepPickupDatetime;

  @NotBlank(message = "tpepDropoffDatetime datetime is mandatory.")
  @DateTimeFormat(pattern = "M/d/yyyy h:mm:ss a", message = "tpepDropoffDatetime invalid datatime format.")
  private String tpepDropoffDatetime;

  @NotNull(message = "dropOffDay day is mandatory.")
  @Positive
  private Integer dropOffDay;

  @NotNull(message = "dropOffMonth month is mandatory.")
  @Positive
  private Integer dropOffMonth;

  private Integer dropOffYear;

  @Min(value = 1, message = "There must be at least 1 passenger, passengerCount must be present.")
  private Integer passengerCount;

  @NotNull
  @PositiveOrZero(message = "tripDistance must be a positive value.")
  private BigDecimal tripDistance;

  @NotNull(message = "puLocationId is mandatory.")
  private Integer puLocationId;

  @NotNull(message = "doLocationId is mandatory.")
  private Integer doLocationId;


  private Boolean storeAndFwdFlag;

  @NotNull(message = "fareAmount is mandatory.")
  private BigDecimal fareAmount;

  @NotNull(message = "extra is mandatory.")
  private BigDecimal extra;

  @NotNull(message = "mtaTax is mandatory.")
  private BigDecimal mtaTax;

  @NotNull(message = "improvementSurcharge is mandatory.")
  private BigDecimal improvementSurcharge;

  @NotNull(message = "tipAmount is mandatory.")
  private BigDecimal tipAmount;

  @NotNull(message = "tollsAmount is mandatory.")
  private BigDecimal tollsAmount;

  @NotNull(message = "totalAmount is mandatory.")
  @Positive
  private BigDecimal totalAmount;

  // relations

  private Long vendorId;

  private Long rateCodeId;

  private Long paymentTypeId;

  public void setDropOffDayMonthYear() {
    LocalDateTime dateTime = LocalDateTime.parse(this.tpepDropoffDatetime, DATE_TIME_FORMATTER);
    this.dropOffDay = dateTime.getDayOfMonth();
    this.dropOffMonth = dateTime.getMonthValue();
    this.dropOffYear = dateTime.getYear();
  }
}
