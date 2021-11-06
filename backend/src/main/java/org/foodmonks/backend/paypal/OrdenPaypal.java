package org.foodmonks.backend.paypal;

import lombok.Data;

import java.net.URI;

@Data
public class OrdenPaypal {
    private final String orderId;
    private final URI linkAprobacion;
    private final URI linkDevolucion;

}
