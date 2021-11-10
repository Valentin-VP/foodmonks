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

    public JsonObject jsonAdmin(Admin admin) {
        JsonObject jsonAdmin = new JsonObject();
        jsonAdmin.addProperty("correo", admin.getCorreo());
        jsonAdmin.addProperty("nombre", admin.getNombre());
        jsonAdmin.addProperty("apellido", admin.getApellido());
        jsonAdmin.addProperty("fechaRegistro", admin.getFechaRegistro().toString());
        JsonArray jsonRoles = new JsonArray();
        JsonObject jsonRol = new JsonObject();
        jsonRol.addProperty("role", admin.getRoles());
        jsonRoles.add(jsonRol);
        jsonAdmin.add("roles", jsonRoles);
        return jsonAdmin;
    }
}
