package org.foodmonks.backend.Pedido;

import com.google.gson.JsonObject;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.datatypes.EstadoPedido;
import org.foodmonks.backend.datatypes.MedioPago;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoConvertidor pedidoConvertidor;

    @Autowired
    public PedidoService(PedidoRepository pedidoRepository, PedidoConvertidor pedidoConvertidor){
        this.pedidoRepository = pedidoRepository; this.pedidoConvertidor = pedidoConvertidor;
    }

    public List<JsonObject> listaPedidosPendientes(Restaurante restaurante){
        return pedidoConvertidor.listaJsonPedidoPendientes(pedidoRepository.findPedidosByRestauranteAndEstado(restaurante, EstadoPedido.PENDIENTE));
    }

    public boolean existePedidoRestaurante (Long idPedido, Restaurante restaurante) {
        return pedidoRepository.existsPedidoByIdAndRestaurante(idPedido,restaurante); }

    public void cambiarEstadoPedido(Long idPedido, EstadoPedido estadoPedido){
        Pedido pedido = pedidoRepository.findPedidoById(idPedido);
        pedido.setEstado(estadoPedido);
        pedidoRepository.save(pedido);
    }

    // Para cuando se confirma el pedido.
    public void cambiarFechasEntregaProcesado(Long idPedido, Integer minutosEntrega){
        Pedido pedido = pedidoRepository.findPedidoById(idPedido);
        pedido.setFechaHoraProcesado(LocalDateTime.now());
        pedido.setFechaHoraEntrega(pedido.getFechaHoraProcesado().plusMinutes(minutosEntrega));
        pedidoRepository.save(pedido);
    }
}
