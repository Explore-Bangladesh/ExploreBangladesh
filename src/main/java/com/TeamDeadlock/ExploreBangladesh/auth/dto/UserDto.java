package com.TeamDeadlock.ExploreBangladesh.auth.dto;

import com.TeamDeadlock.ExploreBangladesh.auth.entity.Provider;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private UUID id;

    private String email;

    private String firstName;

    private String lastName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String phone;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    private String country;

    private Boolean subscribeNewsletter;

    private String image;

    private Boolean enable;

    private Provider provider;
}
