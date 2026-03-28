package com.TeamDeadlock.ExploreBangladesh.auth.service.impl;

import com.TeamDeadlock.ExploreBangladesh.auth.dto.UserDto;
import com.TeamDeadlock.ExploreBangladesh.auth.service.AuthService;
import com.TeamDeadlock.ExploreBangladesh.auth.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto registerUser(UserDto userDto) {
        // Ensure newly registered users are enabled and password is encoded
        if (userDto.getEnable() == null) {
            userDto.setEnable(true);
        }
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userService.createUser(userDto);
    }
}
