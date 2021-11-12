package org.foodmonks.backend.Pedido;

import org.foodmonks.backend.Pedido.Exceptions.PedidoNoExisteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    @Autowired
    public PedidoService (PedidoRepository pedidoRepository){
        this.pedidoRepository = pedidoRepository;
    }

    public Pedido obtenerPedido(Long id) throws PedidoNoExisteException {
        Pedido pedido = pedidoRepository.findPedidoById(id);
        if (pedido == null) {
            throw new PedidoNoExisteException("No existe pedido con id " + id);
        }
        return pedido;
    }

}
