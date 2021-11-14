package org.foodmonks.backend.Pedido;

import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.Restaurante.RestauranteRepository;
import org.foodmonks.backend.datatypes.EstadoPedido;
import org.foodmonks.backend.datatypes.EstadoRestaurante;
import org.foodmonks.backend.datatypes.MedioPago;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class PedidoConfig {
/*    @Bean
    CommandLineRunner commandLineRunnerPedido(PedidoRepository pedidoRepository, RestauranteRepository restauranteRepository, PasswordEncoder passwordEncoder) {
        return args ->{

            Direccion dir = new Direccion(111, "calle1", "calle2", null, "20", "20");
            Float calificacion = 5.0f;
            LocalDate ahora = LocalDate.now();
            Restaurante  restaurante =  new Restaurante("nombreDelRestaurante",
                    "apellidoDelRestaurante", "restaurante@gmail.com",
                    passwordEncoder.encode("restaurante123"), ahora, calificacion, "NombreRestaurante", 123456, dir, EstadoRestaurante.ABIERTO, 23487123, "DescripcionRestaurante", "CuentaDePaypal", null);
            restauranteRepository.saveAll(List.of(restaurante));

            Pedido pedido = new Pedido(EstadoPedido.CONFIRMADO,Float.valueOf("200"), MedioPago.EFECTIVO);
            pedido.setRestaurante(restaurante);
            //pedido.setDireccion(dir);

            pedidoRepository.save(pedido);
            // ejemplo dar de alta pedido

        };
    }*/
}
