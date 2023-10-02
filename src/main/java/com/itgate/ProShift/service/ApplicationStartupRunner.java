package com.itgate.ProShift.service;

import com.itgate.ProShift.entity.ERole;
import com.itgate.ProShift.entity.Role;
import com.itgate.ProShift.entity.User;
import com.itgate.ProShift.repository.RoleRepository;
import com.itgate.ProShift.repository.UserRepository;
import com.itgate.ProShift.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;
    @Value("${admin.email}")
    private String adminEmail;
    public ApplicationStartupRunner(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if the admin role exists
        System.out.println(adminEmail);
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).get();
        if (adminRole == null) {
            // Create the "admin" role if it doesn't exist
            adminRole = new Role();
            adminRole.setName(ERole.ROLE_ADMIN);
            roleRepository.save(adminRole);
        }
        System.out.println("here");
        // Check if the admin user exists
        if(!userRepository.findByUsername(adminUsername).isPresent()){
            // Create the default admin user
            User adminUser = adminUser = new User();
            adminUser.setUsername(adminUsername);
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode(adminPassword)); // Hash the password
            adminUser.getRoles().add(adminRole);
            userRepository.save(adminUser);
        }
    }
}
