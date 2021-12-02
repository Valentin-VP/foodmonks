package org.foodmonks.backend.Cliente;

import org.foodmonks.backend.authentication.TokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteHelper {

    private TokenHelper tokenHelp;

    @Autowired
    public ClienteHelper(TokenHelper tokenHelp) {
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
