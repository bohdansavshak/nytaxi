package com.bohdansavshak.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "taxi_trip")
public class TaxiTrip {

  @Id
  @Column(name = "taxi_trip_id")
  private Long id;
}
