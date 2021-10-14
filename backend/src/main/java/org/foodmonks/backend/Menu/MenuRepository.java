package org.foodmonks.backend.Menu;

import org.foodmonks.backend.Restaurante.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    Menu findByIdAndRestaurante(Long id, Restaurante restaurante);
    Boolean existsByNombreAndRestaurante(String name, Restaurante restaurante);
}
