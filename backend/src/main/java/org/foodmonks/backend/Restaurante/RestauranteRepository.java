package org.foodmonks.backend.Restaurante;

import org.foodmonks.backend.datatypes.EstadoRestaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, String> {

    Restaurante findByCorreo(String correo);
    List<Restaurante> findRestaurantesByNombreRestauranteContainsAndEstado(String nombreRestaurante, EstadoRestaurante estadoRestaurante);
    List<Restaurante> findRestaurantesByNombreRestauranteContainsAndEstadoOrderByCalificacionDesc(String nombreRestaurante, EstadoRestaurante estadoRestaurante);
    List<Restaurante> findRestaurantesByEstado(EstadoRestaurante estadoRestaurante);
    List<Restaurante> findRestaurantesByEstadoOrderByCalificacionDesc(EstadoRestaurante estadoRestaurante);
    List<Restaurante> findAllByRolesOrderByCalificacion(String role);
    Long countRestaurantesByFechaRegistroBetween(LocalDate fechaIni, LocalDate fechaFin);
    Long countRestaurantesByEstado(EstadoRestaurante estadoRestaurante);
    List<Restaurante> findAllByRolesOrderByCalificacionDesc(String role);
}
