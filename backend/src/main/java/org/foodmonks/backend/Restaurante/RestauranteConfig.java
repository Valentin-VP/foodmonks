package org.foodmonks.backend.Restaurante;

import java.time.LocalDate;
import java.util.List;

import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.Menu.Menu;
import org.foodmonks.backend.Menu.MenuRepository;
import org.foodmonks.backend.datatypes.CategoriaMenu;
import org.foodmonks.backend.datatypes.EstadoRestaurante;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class RestauranteConfig {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MenuRepository menuRepository;

    @Bean
    CommandLineRunner commandLineRunnerRestaurante(RestauranteRepository repository) {
        return args ->{
            Direccion dir = new Direccion(111, "calle1", "calle2", null, "2", "2");
            Float calificacion = 5.0f;
            LocalDate ahora = LocalDate.now();
            Restaurante  restaurante =  new Restaurante("nombreDelRestaurante",
                    "apellidoDelRestaurante", "restaurante@gmail.com",
                    passwordEncoder.encode("admin123"), ahora, calificacion, "NombreRestaurante", 123456, dir, EstadoRestaurante.ABIERTO, 23487123, "DescripcionRestaurante", "CuentaDePaypal", null);
            //ejemplo para dar de alta un restaurante

            Menu menu = new Menu("papas fritas",123.34F,"",true,0F,"", CategoriaMenu.OTROS);
            menu.setRestaurante(restaurante);
            // ejemplo dar de alta menu

            menuRepository.save(menu);
            repository.saveAll(List.of(restaurante));
        };
    }

}
