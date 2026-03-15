package com.TeamDeadlock.ExploreBangladesh.auth.helpers;

import java.util.UUID;

public class UserHelper {

    public static UUID parseUUID(String userId) {
        try {
            return UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user ID format: " + userId);
        }
    }
}
