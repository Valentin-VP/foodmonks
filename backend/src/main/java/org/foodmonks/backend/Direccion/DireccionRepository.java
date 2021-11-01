package org.foodmonks.backend.Direccion;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DireccionRepository extends JpaRepository<Direccion, String> {

    Direccion findDireccionByLatitudAndLongitud(String latitud, String longitud);

}
