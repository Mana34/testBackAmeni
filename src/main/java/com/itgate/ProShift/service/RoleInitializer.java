package com.itgate.ProShift.service;

import com.itgate.ProShift.entity.ERole;
import com.itgate.ProShift.entity.Role;
import com.itgate.ProShift.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private ERole eRole;
    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Check if the roles already exist in the database
        if (roleRepository.findByName(ERole.ROLE_USER) == null) {
            Role roleUser = new Role();
            roleUser.setName(ERole.ROLE_ADMIN);
            roleRepository.save(roleUser);
        }

        if (roleRepository.findByName(ERole.ROLE_ADMIN) == null) {
            Role roleAdmin = new Role();
            roleAdmin.setName(ERole.ROLE_ADMIN);
            roleRepository.save(roleAdmin);
        }
    }
}
