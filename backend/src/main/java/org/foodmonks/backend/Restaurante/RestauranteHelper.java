package org.foodmonks.backend.Restaurante;

import org.foodmonks.backend.authentication.TokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class RestauranteHelper {

    private TokenHelper tokenHelp;
    
    @Autowired
    public RestauranteHelper(TokenHelper tokenHelp) {
        this.tokenHelp = tokenHelp;
    }
    
    public String obtenerCorreoDelToken(String token){
        String parsedToken = null;
        if ( token != null && token.startsWith("Bearer ")) {
            parsedToken = token.substring(7);
        }
        return tokenHelp.getUsernameFromToken(parsedToken);
    }
}
