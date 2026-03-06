package com.TeamDeadlock.ExploreBangladesh.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "airlines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Airline {

    @Id
    @Column(name = "code", length = 3)
    private String code;

    @Column(nullable = false)
    private String name;
}
