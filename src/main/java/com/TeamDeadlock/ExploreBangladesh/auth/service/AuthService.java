package com.TeamDeadlock.ExploreBangladesh.auth.service;

import com.TeamDeadlock.ExploreBangladesh.auth.dto.UserDto;

public interface AuthService {

    // register user
    UserDto registerUser(UserDto userDto);

    // login user
}
