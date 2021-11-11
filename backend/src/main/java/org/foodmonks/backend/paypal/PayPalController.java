package org.foodmonks.backend.paypal;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

@Controller
@RequestMapping("/api/v1/paypal")
@Slf4j
public class PayPalController {

    private final PayPalService payPalService;

    @Autowired
    public PayPalController(PayPalService payPalService) {
        this.payPalService = payPalService;
    }

    @PostMapping("/order/request")
    public String orderRequest(@RequestBody String orden, HttpServletRequest request){
        JsonObject detallesOrden = new Gson().fromJson(orden, JsonObject.class).getAsJsonObject();
        Double total = detallesOrden.get("total").getAsDouble();
        log.info("Total: " + total);
        final URI callbackUrl = callbackUrl(request);
        log.info("callbackUrl: " + callbackUrl);
        OrdenPaypal ordenPaypal = payPalService.orderRequest(total, callbackUrl);
        return "redirect:"+ ordenPaypal.getLinkAprobacion();
    }

    @GetMapping("/order/capture")
    public String captureOrder(@RequestParam String orderData){
        payPalService.captureOrder(orderData);
        return "redirect:/orders";
    }


    private URI callbackUrl(HttpServletRequest request) {
        try {
            URI requestUri = URI.create(request.getRequestURL().toString());
            return new URI(requestUri.getScheme(),
                    requestUri.getUserInfo(),
                    requestUri.getHost(),
                    requestUri.getPort(),
                    "/api/v1/paypal/orders/capture",
                    null, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
