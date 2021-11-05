package org.foodmonks.backend.paypal;

import java.net.URI;

public interface PaymentService {

    OrdenPaypal orderRequest(Double totalAmount, URI linkAprobacion, URI linkDevolucion);

    void captureOrder(String orderId);
}
