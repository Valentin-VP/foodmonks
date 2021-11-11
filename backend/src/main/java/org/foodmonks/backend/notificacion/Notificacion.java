package org.foodmonks.backend.notificacion;

import lombok.Data;

@Data
public class Notificacion {
    private String tokenExpo;
    private String asunto;
    private String message;

}
