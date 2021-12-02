package org.foodmonks.backend.Restaurante;

import org.foodmonks.backend.datatypes.EstadoRestaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, String> {

    Restaurante findByCorreoIgnoreCase(String correo);
    Restaurante findRestaurantesByCorreoIgnoreCase(String correo);
    List<Restaurante> findRestaurantesByNombreRestauranteIgnoreCaseContainsAndEstado(String nombreRestaurante, EstadoRestaurante estadoRestaurante);
    List<Restaurante> findRestaurantesByNombreRestauranteIgnoreCaseContainsAndEstadoOrderByCalificacionDesc(String nombreRestaurante, EstadoRestaurante estadoRestaurante);
    List<Restaurante> findRestaurantesIgnoreCaseByEstado(EstadoRestaurante estadoRestaurante);
    List<Restaurante> findRestaurantesIgnoreCaseByEstadoOrderByCalificacionDesc(EstadoRestaurante estadoRestaurante);
    List<Restaurante> findAllByRolesOrderByCalificacion(String role);
    Long countRestaurantesByFechaRegistroBetween(LocalDate fechaIni, LocalDate fechaFin);
    Long countRestaurantesByEstado(EstadoRestaurante estadoRestaurante);
    List<Restaurante> findAllByRolesOrderByCalificacionDesc(String role);
}
