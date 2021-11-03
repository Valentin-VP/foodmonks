package org.foodmonks.backend.Direccion;

import com.google.gson.JsonObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DireccionConverter {

    public List<JsonObject> listaJsonDireccion(List<Direccion> direcciones){
        List<JsonObject> gsonDirecciones = new ArrayList<>();
        for (Direccion direccion : direcciones){
            gsonDirecciones.add(jsonDireccion(direccion));
        }
        return gsonDirecciones;
    }

    public JsonObject jsonDireccion(Direccion direccion) {
        JsonObject jsonDireccion = new JsonObject();
        jsonDireccion.addProperty("numero", direccion.getNumero());
        jsonDireccion.addProperty("calle",direccion.getCalle());
        jsonDireccion.addProperty("esquina",direccion.getEsquina());
        jsonDireccion.addProperty("detalles",direccion.getDetalles());
        jsonDireccion.addProperty("latitud",direccion.getLatitud());
        jsonDireccion.addProperty("longitud",direccion.getLongitud());
        return jsonDireccion;
    }

    public Direccion direccionJson (JsonObject jsonDireccion) {
        return new Direccion(
                jsonDireccion.get("numero").getAsInt(),
                jsonDireccion.get("calle").getAsString(),
                jsonDireccion.get("esquina").getAsString(),
                jsonDireccion.get("detalles").getAsString(),
                jsonDireccion.get("latitud").getAsString(),
                jsonDireccion.get("longitud").getAsString()
        );
    }

}
