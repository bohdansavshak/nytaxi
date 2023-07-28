package com.bohdansavshak.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class TaxiTrip {

    @Id
    @Column(name = "taxi_trip_id")
    private Long id;

}
