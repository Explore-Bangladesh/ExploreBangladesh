package com.TeamDeadlock.ExploreBangladesh.auth.config;

import com.TeamDeadlock.ExploreBangladesh.auth.entity.Role;
import com.TeamDeadlock.ExploreBangladesh.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@RequiredArgsConstructor
public class RoleInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(ApplicationArguments args) {
        seedRole(AppConstants.ADMIN_ROLE);
        seedRole(AppConstants.GUEST_ROLE);
    }

    private void seedRole(String roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            roleRepository.save(Role.builder().name(roleName).build());
            System.out.println("[RoleInitializer] Seeded role: " + roleName);
        }
    }
}
