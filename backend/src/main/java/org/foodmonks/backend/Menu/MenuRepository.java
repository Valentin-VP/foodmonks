package org.foodmonks.backend.Menu;

import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.datatypes.CategoriaMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    Menu findByIdAndRestaurante(Long id, Restaurante restaurante);
    Boolean existsByNombreAndRestaurante(String name, Restaurante restaurante);
    List<Menu> findMenusByRestaurante(Restaurante restaurante);
    List<Menu> findMenuByCategoria(CategoriaMenu categoriaMenu);
    Boolean existsMenuByRestauranteAndCategoria(Restaurante restaurante, CategoriaMenu categoriaMenu);
    List<Menu> findMenuByPriceBetween(Float precioInicial, Float precioFinal);
}
