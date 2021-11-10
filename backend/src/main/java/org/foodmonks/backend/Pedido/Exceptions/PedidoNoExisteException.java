package org.foodmonks.backend.Pedido.Exceptions;

public class PedidoNoExisteException extends Exception {
    public PedidoNoExisteException(String errorMessage) { super(errorMessage); }
}
