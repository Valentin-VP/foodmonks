package org.foodmonks.backend.Restaurante;

import java.time.LocalDate;
import java.util.List;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.datatypes.EstadoRestaurante;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class RestauranteConfig {

    @Bean
    CommandLineRunner commandLineRunnerRestaurante(RestauranteRepository restauranteRepository, PasswordEncoder passwordEncoder) {
        return args ->{
            Direccion dir = new Direccion(111, "calle1", "calle2", null, "2", "2");
            Float calificacion = 5.0f;
            LocalDate ahora = LocalDate.now();
            Restaurante  restaurante =  new Restaurante("nombreDelRestaurante",
                    "apellidoDelRestaurante", "restaurante@gmail.com",
                    passwordEncoder.encode("restaurante123"), ahora, calificacion,0, "NombreRestaurante", 123456, dir, EstadoRestaurante.ABIERTO, 23487123, "DescripcionRestaurante", "CuentaDePaypal", null);
            restauranteRepository.saveAll(List.of(restaurante));
            //ejemplo para dar de alta un restaurante
        };
    }

}
