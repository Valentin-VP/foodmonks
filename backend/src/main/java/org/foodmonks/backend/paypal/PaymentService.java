package org.foodmonks.backend.paypal;

import java.net.URI;

public interface PaymentService {

    OrdenPaypal orderRequest(Double totalAmount, URI linkAprobacion);

    void captureOrder(String orderId);

}
