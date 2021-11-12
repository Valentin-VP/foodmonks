package org.foodmonks.backend.Reclamo;

import org.foodmonks.backend.Pedido.Pedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ReclamoService {

    private final ReclamoRepository reclamoRepository;

    @Autowired
    public ReclamoService(ReclamoRepository reclamoRepository){
        this.reclamoRepository = reclamoRepository;
    }

    public void crearReclamo(String razon, String comentario, LocalDate fecha, Pedido pedido){
        reclamoRepository.save(new Reclamo(razon,comentario,fecha,pedido));
    }

}
