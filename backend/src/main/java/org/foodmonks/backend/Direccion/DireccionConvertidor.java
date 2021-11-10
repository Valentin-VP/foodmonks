package org.foodmonks.backend.Direccion;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class DireccionConvertidor {

    public List<JsonObject> listaJsonDireccion(List<Direccion> direcciones){
        List<JsonObject> gsonDirecciones = new ArrayList<>();
        for (Direccion direccion : direcciones){
            gsonDirecciones.add(jsonDireccion(direccion));
        }
        return gsonDirecciones;
    }

    public JsonObject jsonDireccion(Direccion direccion) {
        JsonObject jsonDireccion= new JsonObject();
        jsonDireccion.addProperty("numero", direccion.getNumero());
        jsonDireccion.addProperty("calle", direccion.getCalle());
        jsonDireccion.addProperty("esquina", direccion.getCalle());
        jsonDireccion.addProperty("detalles", direccion.getDetalles());
        jsonDireccion.addProperty("latitud", direccion.getLatitud());
        jsonDireccion.addProperty("longitud", direccion.getLongitud());
        return jsonDireccion;
    }

}
