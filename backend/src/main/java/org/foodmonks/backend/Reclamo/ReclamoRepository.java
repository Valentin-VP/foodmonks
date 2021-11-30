package org.foodmonks.backend.Reclamo;

import org.foodmonks.backend.Pedido.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReclamoRepository extends JpaRepository<Reclamo,Long> {

    Reclamo findReclamoByPedido(Pedido pedido);

}
