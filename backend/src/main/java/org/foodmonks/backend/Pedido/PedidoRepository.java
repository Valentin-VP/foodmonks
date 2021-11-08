package org.foodmonks.backend.Pedido;

import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.datatypes.EstadoPedido;
import org.foodmonks.backend.datatypes.MedioPago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long>, JpaSpecificationExecutor<Pedido> {

    Pedido findPedidoById(Long id);
    List<Pedido> findPedidosByRestauranteAndEstado(Restaurante restaurante, EstadoPedido estadoPedido);
    Boolean existsPedidoByIdAndRestaurante(Long id, Restaurante restaurante);
    Page<Pedido> findPedidosByRestaurante(Restaurante restaurante, Pageable pageable);
    Page<Pedido> findAll(Specification<Pedido> spec, Pageable pageable);
}
