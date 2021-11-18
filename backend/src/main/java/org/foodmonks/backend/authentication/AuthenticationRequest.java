package org.foodmonks.backend.authentication;

import java.util.Base64;

public class AuthenticationRequest {

    private String email;
    private String password;
    private String mobileToken;

    public String getEmail() {
        byte[] decodedBytesMail = Base64.getDecoder().decode(this.email);
        return new String(decodedBytesMail);
    }

    public String getPassword() {
        byte[] decodedBytesPass = Base64.getDecoder().decode(this.password);
        return new String(decodedBytesPass);
    }

    public String getMobileToken() {
        byte[] decodedBytesMobileToken = Base64.getDecoder().decode(this.mobileToken);
        return new String(decodedBytesMobileToken);
    }
}
