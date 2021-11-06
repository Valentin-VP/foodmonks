package org.foodmonks.backend.Pedido;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.foodmonks.backend.MenuCompra.MenuCompra;
import org.foodmonks.backend.datatypes.MedioPago;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Component
public class PedidoConvertidor {

    // Para Listar pedidos sin confirmar
    public List<JsonObject> listaJsonPedidoPendientes(List<Pedido> pedidos){
        List<JsonObject> gsonPedidos = new ArrayList<>();
        for (Pedido pedido : pedidos){
            gsonPedidos.add(jsonPedidoPendientes(pedido));
        }
        return gsonPedidos;
    }

    // Datos de MenuCompra y Nombre y Apellido del cliente + datos estándar de pedido
    public JsonObject jsonPedidoPendientes(Pedido pedido) {
        JsonObject jsonPedido= new JsonObject();
        jsonPedido.addProperty("id", pedido.getId());
        jsonPedido.addProperty("nombre", pedido.getNombre());
        jsonPedido.addProperty("direccion", pedido.getDireccion().getCalle() + " "
                + pedido.getDireccion().getNumero().toString() + " esq. "
                + pedido.getDireccion().getEsquina()
                + (pedido.getDireccion().getDetalles()!=null ? " (" + pedido.getDireccion().getDetalles()+ ")" : ""));
        jsonPedido.addProperty("total", pedido.getTotal());
        jsonPedido.addProperty("medioPago", pedido.getMedioPago() == MedioPago.PAYPAL ? "PayPal" : "Efectivo");

        if (pedido.getCliente()!=null){
            jsonPedido.addProperty("nombreApellidoCliente", pedido.getCliente().getNombre() + " " + pedido.getCliente().getApellido());
        }

        if (!pedido.getMenusCompra().isEmpty()){
            JsonArray jsonMenus = new JsonArray();
            for (MenuCompra m : pedido.getMenusCompra()) {
                JsonObject jsonMenuCompra= new JsonObject();
                jsonMenuCompra.addProperty("menu", m.getNombre());
                jsonMenuCompra.addProperty("imagen", m.getImagen());
                jsonMenuCompra.addProperty("precio", m.getPrice().toString());
                jsonMenuCompra.addProperty("multiplicadorPromocion", m.getMultiplicadorPromocion()*100);
                jsonMenuCompra.addProperty("calculado", Math.round(m.getPrice() * (1 - m.getMultiplicadorPromocion()) * 100) / 100);
                jsonMenus.add(jsonMenuCompra);
            }
            jsonPedido.add("menus", jsonMenus);
        }

        return jsonPedido;
    }
}
