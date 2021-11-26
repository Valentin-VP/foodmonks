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
    List<Pedido> findPedidosByRestauranteAndEstado(Restaurante restaurante, EstadoPedido estadoPedido);
    Boolean existsPedidoByIdAndRestaurante(Long id, Restaurante restaurante);
    Page<Pedido> findPedidosByRestaurante(Restaurante restaurante, Pageable pageable);
    List<Pedido> findPedidosByRestaurante(Restaurante restaurante);
    Page<Pedido> findAll(Specification<Pedido> spec, Pageable pageable);
    List<Pedido> findPedidosByRestauranteAndEstadoAndMedioPago(Restaurante restaurante, EstadoPedido estadoPedido, MedioPago medioPago);
    Boolean existsPedidoById(Long id);
    Long countPedidosByRestauranteAndFechaHoraProcesadoBetween(Restaurante restaurante, LocalDateTime fechaIni, LocalDateTime fechaFin);
    Long countPedidosByRestauranteAndEstadoAndFechaHoraProcesadoBetween(Restaurante restaurante, EstadoPedido estadoPedido, LocalDateTime fechaIni, LocalDateTime fachaFin);

}
