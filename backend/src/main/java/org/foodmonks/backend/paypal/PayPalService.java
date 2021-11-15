package org.foodmonks.backend.paypal;

import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.util.Arrays;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class PayPalService implements PaymentService{

    private final String APPROVE_LINK_REL = "approve";
    private final String DEVOLUTION_LINK_REL = "";

    private final PayPalHttpClient payPalHttpClient;

    @Autowired
    public PayPalService (PayPalConfig payPalConfig){
        this.payPalHttpClient = payPalConfig.client;
    }

    @SneakyThrows
    @Override
    public OrdenPaypal orderRequest(Double total, URI returnUrl) {
        final OrderRequest orderRequest = prepareOrderRequest(total, returnUrl);
        final OrdersCreateRequest ordersCreateRequest = new OrdersCreateRequest().requestBody(orderRequest);
        final HttpResponse<Order> orderHttpResponse = payPalHttpClient.execute(ordersCreateRequest);
        final Order order = orderHttpResponse.result();
        LinkDescription approveUri = extractApprovalLink(order);
        return new OrdenPaypal(order.id(),URI.create(approveUri.href()),null);
    }

    private OrderRequest prepareOrderRequest(Double total, URI callbackUrl) {
        final OrderRequest orderRequest = new OrderRequest();
        setCheckoutIntent(orderRequest);
        setPurchaseUnits(total, orderRequest);
        setApplicationContext(callbackUrl, orderRequest);
        return orderRequest;
    }

    private OrderRequest setApplicationContext(URI callbackUrl, OrderRequest orderRequest) {
        return orderRequest.applicationContext(new ApplicationContext().returnUrl(callbackUrl.toString()));
    }

    private void setPurchaseUnits(Double totalAmount, OrderRequest orderRequest) {
        final PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest()
                .amountWithBreakdown(new AmountWithBreakdown().currencyCode("USD").value(totalAmount.toString()));
        orderRequest.purchaseUnits(Arrays.asList(purchaseUnitRequest));
    }

    private void setCheckoutIntent(OrderRequest orderRequest) {
        orderRequest.checkoutPaymentIntent("CAPTURE");
    }

    private LinkDescription extractApprovalLink(Order order) {
        return order.links().stream()
                .filter(link -> APPROVE_LINK_REL.equals(link.rel()))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    @SneakyThrows
    public void captureOrder(String orderId) {
        final OrdersCaptureRequest ordersCaptureRequest = new OrdersCaptureRequest(orderId);
        final HttpResponse<Order> httpResponse = payPalHttpClient.execute(ordersCaptureRequest);
        log.info("Order Capture Status: {}",httpResponse.result().status());
    }

}
