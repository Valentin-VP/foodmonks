package org.foodmonks.backend.Cliente;

import org.foodmonks.backend.datatypes.EstadoCliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.util.List;

@Configuration
public class ClienteConfig {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ClienteConfig (PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    CommandLineRunner commandLineRunnerCliente(ClienteRepository repository) {
        return args ->{
//            Cliente cliente =  new Cliente("nombreDelCliente",
//                    "apellidoDelCliente",
//                    "cliente@gmail.com", passwordEncoder.encode("cliente123"), LocalDate.now(), 5.0f,0, null, EstadoCliente.ACTIVO, null, null);
//            repository.saveAll(List.of(cliente));
        };
    }

}
