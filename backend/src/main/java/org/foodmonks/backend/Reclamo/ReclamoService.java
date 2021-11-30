package org.foodmonks.backend.Reclamo;

import com.google.gson.JsonObject;
import org.foodmonks.backend.Pedido.Exceptions.PedidoSinRestauranteException;
import org.foodmonks.backend.Pedido.Pedido;
import org.foodmonks.backend.Pedido.PedidoService;
import org.foodmonks.backend.Reclamo.Exceptions.ReclamoExisteException;
import org.foodmonks.backend.Reclamo.Exceptions.ReclamoNoFinalizadoException;
import org.foodmonks.backend.Restaurante.RestauranteService;
import org.foodmonks.backend.datatypes.EstadoPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class ReclamoService {

    private final ReclamoConverter reclamoConverter;
    private final RestauranteService restauranteService;
    private final ReclamoRepository reclamoRepository;
    private final PedidoService pedidoService;


    @Autowired
    public ReclamoService(ReclamoConverter reclamoConverter,
                          RestauranteService restauranteService,
                          ReclamoRepository reclamoRepository,
                          PedidoService pedidoService){
        this.reclamoConverter = reclamoConverter;
        this.restauranteService = restauranteService;
        this.reclamoRepository = reclamoRepository;
        this.pedidoService = pedidoService;
    }

    public JsonObject crearReclamo(String razon, String comentario, LocalDateTime fecha, Pedido pedido) throws ReclamoNoFinalizadoException, ReclamoExisteException, PedidoSinRestauranteException {
        if (!pedido.getEstado().equals(EstadoPedido.FINALIZADO)){
            throw new ReclamoNoFinalizadoException("No puede reclamar un pedido en proceso o devuelto");
        }
        if (pedido.getReclamo() != null) {
            throw new ReclamoExisteException("Este pedido ya fue reclamado");
        }
        if (pedido.getRestaurante() == null){
            throw new PedidoSinRestauranteException("No existe un restaurante en este pedido");
        }
        Reclamo reclamo = new Reclamo(razon,comentario,fecha,pedido);
        reclamoRepository.save(reclamo);
        Reclamo reclamoAux = reclamoRepository.findReclamoByPedido(pedido);
        restauranteService.agregarReclamoRestaurante(pedido.getRestaurante(), reclamoAux);
        pedidoService.agregarReclamoPedido(pedido,reclamoAux);
        return reclamoConverter.jsonReclamo(reclamoAux);
    }

}
