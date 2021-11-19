package org.foodmonks.backend.Admin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AdminConverter {

    public List<JsonObject> listaAdmins (List<Admin> admins){
        List<JsonObject> gsonAdmins = new ArrayList<>();
        for (Admin admin : admins){
            gsonAdmins.add(jsonAdmin(admin));
        }
        return gsonAdmins;
    }

    public JsonArray arrayJsonAdmin (List<Admin> admins){
        JsonArray arrayJsonAdmins = new JsonArray();
        for (Admin admin : admins){
            arrayJsonAdmins.add(jsonAdmin(admin));
        }
        return arrayJsonAdmins;
    }

    public JsonObject jsonAdmin(Admin admin) {
        JsonObject jsonAdmin = new JsonObject();
        jsonAdmin.addProperty("correo", admin.getCorreo());
        jsonAdmin.addProperty("nombre", admin.getNombre());
        jsonAdmin.addProperty("apellido", admin.getApellido());
        if (admin.getFechaRegistro() != null) {
            jsonAdmin.addProperty("fechaRegistro", admin.getFechaRegistro().toString());
        } else {
            jsonAdmin.addProperty("fechaRegistro", "");
        }
        JsonArray jsonRoles = new JsonArray();
        JsonObject jsonRol = new JsonObject();
        jsonRol.addProperty("role", admin.getRoles());
        jsonRoles.add(jsonRol);
        jsonAdmin.add("roles", jsonRoles);
        return jsonAdmin;
    }
}
