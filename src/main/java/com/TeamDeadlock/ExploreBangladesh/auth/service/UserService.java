package com.TeamDeadlock.ExploreBangladesh.auth.service;

import com.TeamDeadlock.ExploreBangladesh.auth.dto.UserDto;

public interface UserService {

    // create user
    UserDto createUser(UserDto userDto);

    // get user by email
    UserDto getUserByEmail(String email);

    // update user
    UserDto updateUser(UserDto userDto, String userId);

    // delete user
    void deleteUser(String userId);

    // get user by id
    UserDto getUserById(String userId);

    // get all users
    Iterable<UserDto> getAllUsers();
}
