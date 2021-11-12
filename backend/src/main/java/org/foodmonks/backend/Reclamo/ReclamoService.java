package org.foodmonks.backend.Reclamo;

import com.google.gson.JsonObject;
import org.foodmonks.backend.Pedido.Pedido;
import org.foodmonks.backend.Reclamo.Exceptions.ReclamoExisteException;
import org.foodmonks.backend.Reclamo.Exceptions.ReclamoNoFinalizadoException;
import org.foodmonks.backend.datatypes.EstadoPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class ReclamoService {

    private final ReclamoRepository reclamoRepository;
    private final ReclamoConverter reclamoConverter;

    @Autowired
    public ReclamoService(ReclamoRepository reclamoRepository, ReclamoConverter reclamoConverter){
        this.reclamoRepository = reclamoRepository; this.reclamoConverter = reclamoConverter;
    }

    public JsonObject crearReclamo(String razon, String comentario, LocalDate fecha, Pedido pedido) throws ReclamoNoFinalizadoException, ReclamoExisteException {
        if (!pedido.getEstado().equals(EstadoPedido.FINALIZADO)){
            throw new ReclamoNoFinalizadoException("No puede reclamar un pedido en proceso o devuelto");
        }
        if (pedido.getReclamo() != null) {
            throw new ReclamoExisteException("Este pedido ya fue reclamado");
        }
        Reclamo reclamo = new Reclamo(razon,comentario,fecha,pedido);
        reclamoRepository.save(reclamo);
        return reclamoConverter.jsonReclamo(reclamo);
    }

}
