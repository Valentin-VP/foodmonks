package org.foodmonks.backend.Pedido;

import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.datatypes.EstadoPedido;
import org.foodmonks.backend.datatypes.MedioPago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long>, JpaSpecificationExecutor<Pedido> {

    Pedido findPedidoById(Long id);
    Boolean existsPedidoByIdAndRestaurante(Long id, Restaurante restaurante);
    Page<Pedido> findPedidosByRestaurante(Restaurante restaurante, Pageable pageable);
    Page<Pedido> findAll(Specification<Pedido> spec, Pageable pageable);
    Boolean existsPedidoById(Long id);
    List<Pedido> findPedidosByRestaurante(Restaurante restaurante);
    List<Pedido> findPedidosByRestauranteAndEstado(Restaurante restaurante, EstadoPedido estadoPedido);
    List<Pedido> findPedidosByRestauranteAndMedioPago(Restaurante restaurante, MedioPago medioPago);
    List<Pedido> findPedidosByRestauranteAndFechaHoraProcesadoBetween(Restaurante restaurante, LocalDateTime fechaIni, LocalDateTime fechaFin);
    List<Pedido> findPedidosByRestauranteAndEstadoAndMedioPago(Restaurante restaurante, EstadoPedido estadoPedido, MedioPago medioPago);
    List<Pedido> findPedidosByRestauranteAndMedioPagoAndFechaHoraProcesadoBetween(Restaurante restaurante, MedioPago medioPago, LocalDateTime fechaIni, LocalDateTime fechaFin);

}
