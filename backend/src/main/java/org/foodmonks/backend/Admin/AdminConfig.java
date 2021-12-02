package org.foodmonks.backend.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.util.List;

@Configuration
public class AdminConfig {

    private final PasswordEncoder passwordEncoder;

    @Value("${super.admin.username}")
    private String superAdminUsername;

    @Value("${super.admin.password}")
    private String superAdminPassword;

    @Autowired
    public AdminConfig (PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    CommandLineRunner commandLineRunnerAdmin(AdminRepository repository) {
        return args ->{
            Admin admin =  new Admin("Administrador Principal",
                    "Food Monks",
                    superAdminUsername,
                    passwordEncoder.encode(superAdminPassword),
                    LocalDate.now());
            repository.saveAll(List.of(admin));
        };
    }

}
