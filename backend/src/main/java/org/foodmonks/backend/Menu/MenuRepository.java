package org.foodmonks.backend.Menu;

import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.datatypes.CategoriaMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    Menu findByIdAndRestaurante(Long id, Restaurante restaurante);
    Boolean existsMenuByNombreAndRestaurante(String name, Restaurante restaurante);
    List<Menu> findMenusByRestaurante(Restaurante restaurante);
    Boolean existsMenuByRestauranteAndCategoria(Restaurante restaurante, CategoriaMenu categoriaMenu);
    List<Menu> findMenuByRestauranteAndCategoria(Restaurante restaurante, CategoriaMenu categoriaMenu);
}
