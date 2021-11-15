package org.foodmonks.backend.paypal;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PayPalConfig {

    @Value("${paypal.client.id}")
    private String clientId;
    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Bean
    public Map<String, String> paypalSdkConfig() {
        Map<String, String> configMap = new HashMap<>();
        configMap.put("clientId", clientId);
        configMap.put("clientSecret", clientSecret);
        return configMap;
    }

    /**
     * En producción, se usaría PayPalEnvironment.Live() en lugar de PayPalEnvironment.Sandbox()
     */

    @Bean
    public PayPalEnvironment getPayPalEnvironment(){
        return new PayPalEnvironment.Sandbox(
                paypalSdkConfig().get("clientId"),
                paypalSdkConfig().get("clientSecret")
        );
    }

    /**
     *PayPal HTTP client instance with environment that has access
     *credentials context. Use to invoke PayPal APIs.
     */
    PayPalHttpClient client = new PayPalHttpClient(getPayPalEnvironment());

    /**
     *Method to get client object
     *
     *@return PayPalHttpClient client
     */
    public PayPalHttpClient client() {
        return this.client;
    }

}
