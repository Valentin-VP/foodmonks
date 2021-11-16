package org.foodmonks.backend.paypal;

import com.google.gson.Gson;
import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import com.paypal.payments.Capture;
import com.paypal.payments.AuthorizationsCaptureRequest;
import com.paypal.payments.CapturesRefundRequest;
import com.paypal.payments.RefundRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class PayPalService implements PaymentService{

    private final String APPROVE_LINK_REL = "approve";

    private final PayPalHttpClient payPalHttpClient;

    @Autowired
    public PayPalService (PayPalEnvironment getPayPalEnvironment){
        payPalHttpClient = new PayPalHttpClient(getPayPalEnvironment);
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

    // DEVOLUCION

    public HttpResponse<Capture> captureAuth(String authId) throws IOException {
        AuthorizationsCaptureRequest request = new AuthorizationsCaptureRequest(authId);
        request.requestBody(buildRequestBody());
        HttpResponse<Capture> response = payPalHttpClient.execute(request);
        return response;
    }

    public HttpResponse<com.paypal.payments.Refund> refundOrder(String captureId) throws IOException {
        CapturesRefundRequest request = new CapturesRefundRequest(captureId);
        request.prefer("return=representation");
        request.requestBody(buildRequestBody());
        // #3. Call PayPal to refund an capture
        HttpResponse<com.paypal.payments.Refund> response = payPalHttpClient.execute(request);
        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Status: " + response.result().status());
        System.out.println("Refund Id: " + response.result().id());
        System.out.println("Links: ");
        for (com.paypal.payments.LinkDescription link : response.result().links()) {
            System.out.println("\t" + link.rel() + ": " + link.href() + "\tCall Type: " + link.method());
        }
        System.out.println("Full response body:");
        System.out.println(new Gson().fromJson(response.result().toString(), String.class));
        return response;
    }

    // Creating a body for partial refund request.
    // For full refund, pass the empty body.
    //
    // @return OrderRequest request with empty body

    public RefundRequest buildRequestBody() {
        RefundRequest refundRequest = new RefundRequest();
        com.paypal.payments.Money money = new com.paypal.payments.Money();
        money.currencyCode("USD");
        money.value("20.00");
        refundRequest.amount(money);

        return refundRequest;
    }

}
