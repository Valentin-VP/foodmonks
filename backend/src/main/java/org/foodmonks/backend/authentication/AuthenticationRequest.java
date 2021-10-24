package org.foodmonks.backend.authentication;

import java.util.Base64;

public class AuthenticationRequest {

    private String email;
    private String password;
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        byte[] decodedBytesPass = Base64.getDecoder().decode(this.password);
        String decodedPassword = new String(decodedBytesPass);
        return decodedPassword;
    }


}
