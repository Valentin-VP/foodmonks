package org.foodmonks.backend.Reclamo;

import com.google.gson.JsonObject;
import org.springframework.stereotype.Component;

@Component
public class ReclamoConverter {

    public JsonObject jsonReclamo (Reclamo reclamo){
        JsonObject jsonReclamo = new JsonObject();
        jsonReclamo.addProperty("id",reclamo.getId());
        jsonReclamo.addProperty("razon",reclamo.getRazon());
        jsonReclamo.addProperty("comentario",reclamo.getComentario());
        jsonReclamo.addProperty("fecha",reclamo.getFecha().toString());
        jsonReclamo.addProperty("idPedido",reclamo.getPedido().getId());
        return jsonReclamo;
    }

}
