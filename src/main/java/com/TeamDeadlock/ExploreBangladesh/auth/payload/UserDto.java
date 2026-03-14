package com.TeamDeadlock.ExploreBangladesh.auth.payload;

import com.TeamDeadlock.ExploreBangladesh.auth.entity.Provider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private UUID id;
    private String email;
    private String name;
    private String password;
    private String image;
    private Boolean enable;
    private Instant createdAt;
    private Instant updatedAt;
    private Provider provider;
    private Set<RoleDto> roles;
}
