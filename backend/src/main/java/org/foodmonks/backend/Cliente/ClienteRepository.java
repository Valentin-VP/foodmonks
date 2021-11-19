package org.foodmonks.backend.Cliente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, String> {

    Cliente findByCorreo(String correo);
    List<Cliente> findAllByRolesOrderByCalificacionDesc(String role);
    Long countClientesByFechaRegistroBetween(LocalDate fechaIni, LocalDate fechaFin);

}
