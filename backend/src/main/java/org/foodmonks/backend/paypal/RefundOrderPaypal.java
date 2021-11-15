package org.foodmonks.backend.paypal;

import java.io.IOException;

import com.google.gson.Gson;
import com.paypal.http.HttpResponse;
import com.paypal.payments.*;

public class RefundOrderPaypal extends PayPalConfig {

    public HttpResponse<Refund> refundOrder(String captureId, boolean debug) throws IOException {
        CapturesRefundRequest request = new CapturesRefundRequest(captureId);
        request.prefer("return=representation");
        request.requestBody(buildRequestBody());
   // #3. Call PayPal to refund an capture
        HttpResponse<Refund> response = client().execute(request);
        if (debug) {
            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Status: " + response.result().status());
            System.out.println("Refund Id: " + response.result().id());
            System.out.println("Links: ");
            for (LinkDescription link : response.result().links()) {
                System.out.println("\t" + link.rel() + ": " + link.href() + "\tCall Type: " + link.method());
            }
            System.out.println("Full response body:");
            System.out.println(new Gson().fromJson(response.result().toString(), String.class));
        }
        return response;
    }

    // Creating a body for partial refund request.
    // For full refund, pass the empty body.
    //
    // @return OrderRequest request with empty body

    public RefundRequest buildRequestBody() {
        RefundRequest refundRequest = new RefundRequest();
        Money money = new Money();
        money.currencyCode("USD");
        money.value("20.00");
        refundRequest.amount(money);

        return refundRequest;
    }

    // This function initiates capture refund.
    // Replace Capture ID with a valid capture ID.
    //
    // @param args

/*    public static void main(String[] args) {
        try {
            new RefundOrderPaypal().refundOrder("<REPLACE-WITH-VALID-CAPTURE-ID>", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
