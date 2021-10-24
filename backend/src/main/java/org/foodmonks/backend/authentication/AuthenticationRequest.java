package org.foodmonks.backend.authentication;

import java.util.Base64;

public class AuthenticationRequest {

    private String email;
    private String password;
    public String getEmail() {
        byte[] decodedBytesMail = Base64.getDecoder().decode(this.email);
        String decodedMail = new String(decodedBytesMail);
        return decodedMail;
    }

    public String getPassword() {
        byte[] decodedBytesPass = Base64.getDecoder().decode(this.password);
        String decodedPassword = new String(decodedBytesPass);
        return decodedPassword;
    }


}
