package org.foodmonks.backend.Admin;

import org.foodmonks.backend.Cliente.Cliente;
import org.foodmonks.backend.Cliente.ClienteRepository;
import org.foodmonks.backend.datatypes.EstadoCliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class AdminConfig {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner commandLineRunnerAdmin(AdminRepository repository) {
        return args ->{
            Admin admin =  new Admin("nombreDelAdmin",
                    "apellidoDelAdmin",
                    "admin@gmail.com",
                    passwordEncoder.encode("admin123"),
                    LocalDate.now());

            repository.saveAll(List.of(admin));
        };
    }

}
