package org.foodmonks.backend.Menu;

import java.time.LocalDate;
import java.util.List;

import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.Restaurante.RestauranteRepository;
import org.foodmonks.backend.datatypes.CategoriaMenu;
import org.foodmonks.backend.datatypes.EstadoRestaurante;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class MenuConfig {

//    @Bean
//    CommandLineRunner commandLineRunnerMenu(MenuRepository menuRepository, RestauranteRepository restauranteRepository) {
//        return args ->{
//
//            Menu menu = new Menu("papas fritas",123.34F,"",true,0F,"", CategoriaMenu.OTROS);
//            menu.setRestaurante(restauranteRepository.findByCorreoIgnoreCase("restaurante@gmail.com"));
//
//            menuRepository.save(menu);
//            // ejemplo dar de alta menu
//        };
//    }

}
