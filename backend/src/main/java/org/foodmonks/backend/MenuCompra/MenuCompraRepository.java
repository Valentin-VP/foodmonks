package org.foodmonks.backend.MenuCompra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuCompraRepository extends JpaRepository<MenuCompra, Long> {
}
