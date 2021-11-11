package org.foodmonks.backend.Direccion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DireccionRepository extends JpaRepository<Direccion, String> {

    Direccion findDireccionById(Long id);

}
