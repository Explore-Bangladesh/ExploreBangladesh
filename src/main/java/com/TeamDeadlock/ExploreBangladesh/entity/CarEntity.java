package com.TeamDeadlock.ExploreBangladesh.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cars")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarEntity {

    /** Unique car id, e.g. "dhk-1" */
    @Id
    @Column(name = "car_id")
    private String carId;

    @Column(nullable = false)
    private String name;

    /** Type: Sedan, SUV, Micro, Van */
    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Integer seats;

    /** Price per day in BDT */
    @Column(nullable = false)
    private Double price;

    @Column(name = "image_url", length = 512)
    private String imageUrl;

    /** Lowercase city key, e.g. "dhaka", "cox's bazar" */
    @Column(nullable = false)
    private String city;
}
