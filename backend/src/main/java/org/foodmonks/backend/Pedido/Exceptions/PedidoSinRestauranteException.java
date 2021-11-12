package org.foodmonks.backend.Pedido.Exceptions;

public class PedidoSinRestauranteException extends Exception {
    public PedidoSinRestauranteException(String errorMessage) { super(errorMessage); }
}
