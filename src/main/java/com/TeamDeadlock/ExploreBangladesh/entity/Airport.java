package com.TeamDeadlock.ExploreBangladesh.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "airports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Airport {

    @Id
    @Column(name = "iata_code", length = 3)
    private String iataCode;

    @Column(nullable = false)
    private String name;
}
