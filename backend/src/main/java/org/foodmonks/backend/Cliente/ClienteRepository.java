package org.foodmonks.backend.Cliente;

import org.foodmonks.backend.datatypes.EstadoCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, String> {

    Cliente findByCorreoIgnoreCase(String correo);
    List<Cliente> findAllByRolesOrderByCalificacionDesc(String role);
    Long countClientesByFechaRegistroBetween(LocalDate fechaIni, LocalDate fechaFin);
    Long countClientesByEstado(EstadoCliente estadoCliente);
}
