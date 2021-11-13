package org.foodmonks.backend.Reclamo;

import com.google.gson.JsonObject;
import org.foodmonks.backend.Pedido.Exceptions.PedidoSinRestauranteException;
import org.foodmonks.backend.Pedido.Pedido;
import org.foodmonks.backend.Reclamo.Exceptions.ReclamoExisteException;
import org.foodmonks.backend.Reclamo.Exceptions.ReclamoNoFinalizadoException;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.Restaurante.RestauranteService;
import org.foodmonks.backend.datatypes.EstadoPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class ReclamoService {

    private final ReclamoRepository reclamoRepository;
    private final ReclamoConverter reclamoConverter;
    private final RestauranteService restauranteService;

    @Autowired
    public ReclamoService(ReclamoRepository reclamoRepository, ReclamoConverter reclamoConverter,
                          RestauranteService restauranteService){
        this.reclamoRepository = reclamoRepository; this.reclamoConverter = reclamoConverter;
        this.restauranteService = restauranteService;
    }

    public JsonObject crearReclamo(String razon, String comentario, LocalDate fecha, Pedido pedido) throws ReclamoNoFinalizadoException, ReclamoExisteException, PedidoSinRestauranteException {
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
        restauranteService.agregarReclamoRestaurante(pedido.getRestaurante(), reclamo);
        return reclamoConverter.jsonReclamo(reclamo);
    }

}
