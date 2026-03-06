package com.TeamDeadlock.ExploreBangladesh.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "guides")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuideEntity {

    /** Unique guide id, e.g. "g-1" */
    @Id
    @Column(name = "guide_id")
    private String guideId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String city;

    @Column(name = "experience_years", nullable = false)
    private Integer experienceYears;

    @Column(nullable = false)
    private Double rating;

    @Column(name = "image_url", length = 512)
    private String imageUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "guide_languages", joinColumns = @JoinColumn(name = "guide_id"))
    @Column(name = "language")
    private List<String> languages;
}
