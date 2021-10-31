package org.foodmonks.backend.Restaurante;

import org.foodmonks.backend.datatypes.EstadoRestaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, String> {

    Restaurante findByCorreo(String correo);
    List<Restaurante> findRestaurantesByNombreRestauranteAndEstado(String nombreRestaurante, EstadoRestaurante estadoRestaurante);
    List<Restaurante> findRestaurantesByEstadoOrderByCalificacion(EstadoRestaurante estadoRestaurante);
    List<Restaurante> findRestaurantesByEstado(EstadoRestaurante estadoRestaurante);
}
