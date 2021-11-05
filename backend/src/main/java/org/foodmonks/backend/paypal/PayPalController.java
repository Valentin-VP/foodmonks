package org.foodmonks.backend.paypal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

@Controller
@RequestMapping("/api/v1/paypal")
public class PayPalController {

    //private PayPalService payPalService;

    @Autowired
    public PayPalController() {
        //agregar service
    }

    @PostMapping("/order/request")
    public String orderRequest(@RequestParam Double total, HttpServletRequest request){
        final URI callbackUrl = callbackUrl(request);
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
