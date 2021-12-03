package org.foodmonks.backend.Pedido;

import com.google.gson.JsonObject;
import org.foodmonks.backend.Cliente.Cliente;
import org.foodmonks.backend.Pedido.Exceptions.PedidoNoExisteException;
import org.foodmonks.backend.Reclamo.Reclamo;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.datatypes.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.MenuCompra.MenuCompra;
import org.foodmonks.backend.datatypes.DtOrdenPaypal;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoConverter pedidoConverter;

    @Autowired
    public PedidoService(PedidoRepository pedidoRepository, PedidoConverter pedidoConverter){
        this.pedidoRepository = pedidoRepository; this.pedidoConverter = pedidoConverter;
    }

    public List<JsonObject> listaPedidosPendientes(Restaurante restaurante){
        return pedidoConverter.listaJsonPedidoPendientes(pedidoRepository.findPedidosByRestauranteAndEstado(restaurante, EstadoPedido.PENDIENTE));
    }

    public JsonObject listaPedidosHistorico(Restaurante restaurante, EstadoPedido estadoPedido, MedioPago medioPago, String orden, LocalDateTime[] fecha, Float[] total, int page, int size){

        List<Pedido> result;

        PedidoSpecificationBuilder builder = new PedidoSpecificationBuilder();
        List<CriterioQuery> querys = new ArrayList<>();

        if (estadoPedido != null) {
            if (estadoPedido.equals(EstadoPedido.FINALIZADO)){
                querys.add(new CriterioQuery("estado",":",EstadoPedido.RECLAMORECHAZADO, true));
                querys.add(new CriterioQuery("estado",":",estadoPedido, true));
            }else{
                querys.add(new CriterioQuery("estado",":",estadoPedido, false));
            }
        }else{
            querys.add(new CriterioQuery("estado",":",EstadoPedido.DEVUELTO, true));
            querys.add(new CriterioQuery("estado",":",EstadoPedido.FINALIZADO, true));
            querys.add(new CriterioQuery("estado",":",EstadoPedido.RECLAMORECHAZADO, true));
        }
        if (medioPago != null) {
            querys.add(new CriterioQuery("medioPago",":",medioPago, false));
        }

        if (total != null){
            querys.add(new CriterioQuery("total",">",total[0], false));
            querys.add(new CriterioQuery("total","<",total[1], false));
        }

        if (fecha != null){
            querys.add(new CriterioQuery("fechaHoraEntrega",">",fecha[0], false));
            querys.add(new CriterioQuery("fechaHoraEntrega","<",fecha[1].plusDays(1), false));
        }
        querys.add(new CriterioQuery("correo", "p:ru", restaurante.getCorreo(), false));
        for(CriterioQuery c : querys){
            builder.with(c);
        }
        Specification<Pedido> p = builder.build();
        List<Order> orders = new ArrayList<>();
        if (orden != null){
            if (orden.equals("asc"))
                orders.add(new Order(Sort.Direction.ASC, "total"));
            else if (orden.equals("desc"))
                orders.add(new Order(Sort.Direction.DESC, "total"));
            else if (orden.isBlank())
                orders.add(new Order(Sort.Direction.DESC, "id"));
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));
        Page<Pedido> pedidoPage = pedidoRepository.findAll(p, pageable);
        result = pedidoPage.getContent();

        JsonObject jsonObject = pedidoConverter.listaJsonPedidoPaged(result);
        jsonObject.addProperty("currentPage", pedidoPage.getNumber());
        jsonObject.addProperty("totalItems", pedidoPage.getTotalElements());
        jsonObject.addProperty("totalPages", pedidoPage.getTotalPages());
        return jsonObject;
    }

    public JsonObject listaPedidosRealizados(Cliente cliente, EstadoPedido estadoPedido, String nombreMenu, String nombreRestaurante, MedioPago medioPago, String orden, LocalDateTime[] fecha, Float[] total, int page, int size){

        List<Pedido> result;

        PedidoSpecificationBuilder builder = new PedidoSpecificationBuilder();
        List<CriterioQuery> querys = new ArrayList<>();

        if (estadoPedido != null) {
            if (estadoPedido.equals(EstadoPedido.FINALIZADO)){
                querys.add(new CriterioQuery("estado",":",estadoPedido, true));
                querys.add(new CriterioQuery("estado",":",EstadoPedido.RECLAMORECHAZADO, true));
            }else{
                querys.add(new CriterioQuery("estado",":",estadoPedido, false));
            }
        }

        if (nombreRestaurante != null) {
            querys.add(new CriterioQuery("nombreRestaurante","p:ru",nombreRestaurante, false));
        }

        if (nombreMenu != null) {
            querys.add(new CriterioQuery("nombre","p:mc",nombreMenu, false));
        }
        if (medioPago != null) {
            querys.add(new CriterioQuery("medioPago",":",medioPago, false));
        }

        if (total != null){
            querys.add(new CriterioQuery("total",">",total[0], false));
            querys.add(new CriterioQuery("total","<",total[1], false));
        }

        if (fecha != null){
            querys.add(new CriterioQuery("fechaHoraEntrega",">",fecha[0], false));
            querys.add(new CriterioQuery("fechaHoraEntrega","<",fecha[1].plusDays(1), false));
        }
        querys.add(new CriterioQuery("cliente", ":", cliente,false));
        for(CriterioQuery c : querys){
            builder.with(c);
        }
        Specification<Pedido> p = builder.build();
        List<Order> orders = new ArrayList<>();
        if (orden != null){
            if (orden.equals("asc"))
                orders.add(new Order(Sort.Direction.ASC, "total"));
            else if (orden.equals("desc"))
                orders.add(new Order(Sort.Direction.DESC, "total"));
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));
        Page<Pedido> pedidoPage = pedidoRepository.findAll(p, pageable);
        result = pedidoPage.getContent();

        JsonObject jsonObject = pedidoConverter.listaJsonPedidoPaged(result);
        jsonObject.addProperty("currentPage", pedidoPage.getNumber());
        jsonObject.addProperty("totalItems", pedidoPage.getTotalElements());
        jsonObject.addProperty("totalPages", pedidoPage.getTotalPages());
        return jsonObject;
    }


    public List<JsonObject> listaPedidosEfectivoConfirmados(Restaurante restaurante){
        return pedidoConverter.listaJsonPedido(pedidoRepository.findPedidosByRestauranteAndEstadoAndMedioPago(restaurante, EstadoPedido.CONFIRMADO, MedioPago.EFECTIVO));
    }

    public boolean existePedido (Long idPedido){
        return pedidoRepository.existsPedidoById(idPedido);
    }

    public boolean existePedidoRestaurante (Long idPedido, Restaurante restaurante) {
        return pedidoRepository.existsPedidoByIdAndRestaurante(idPedido,restaurante); }

    public void cambiarEstadoPedido(Long idPedido, EstadoPedido estadoPedido){
        Pedido pedido = pedidoRepository.findPedidoById(idPedido);
        pedido.setEstado(estadoPedido);
        pedidoRepository.save(pedido);
    }


    // Para cuando se confirma el pedido.
    public void cambiarFechasEntregaProcesado(Long idPedido, Integer minutosEntrega){
        Pedido pedido = pedidoRepository.findPedidoById(idPedido);
        pedido.setFechaHoraProcesado(LocalDateTime.now());
        pedido.setFechaHoraEntrega(pedido.getFechaHoraProcesado().plusMinutes(minutosEntrega));
        pedidoRepository.save(pedido);
    }

    public JsonObject crearPedido(EstadoPedido estado, Float total, MedioPago medioPago, DtOrdenPaypal ordenPaypal,
                            Direccion direccion, Cliente cliente, Restaurante restaurante, List<MenuCompra> menus) {
        Pedido pedido = new Pedido(estado,total,medioPago);
        if (medioPago.equals(MedioPago.PAYPAL) && ordenPaypal != null){
            pedido.setOrdenPaypal(ordenPaypal);
        }
        pedido.setDireccion(direccion);
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setMenusCompra(menus);
        pedidoRepository.save(pedido);
        return pedidoConverter.jsonPedido(pedido);
    }

    public void agregarReclamoPedido(Pedido pedido, Reclamo reclamo){
        pedido.setReclamo(reclamo);
        pedidoRepository.save(pedido);
    }

    public Long cantPedidosRestaurante(Restaurante restaurante, LocalDateTime fechaIni, LocalDateTime fechaFin){
        return pedidoRepository.countPedidosByRestauranteAndFechaHoraProcesadoBetween(restaurante,fechaIni,fechaFin);
    }

    public Long cantVentasRestauranteAnio(Restaurante restaurante, LocalDateTime fechaIni, LocalDateTime fechaFin){
        return pedidoRepository.countPedidosByRestauranteAndEstadoAndFechaHoraProcesadoBetween(restaurante,EstadoPedido.FINALIZADO,fechaIni,fechaFin) +
                pedidoRepository.countPedidosByRestauranteAndEstadoAndFechaHoraProcesadoBetween(restaurante,EstadoPedido.RECLAMORECHAZADO,fechaIni,fechaFin);  
    }
  
    public void modificarCalificacionRestaurantePedido(Long idPedido, DtCalificacion calificacion) throws PedidoNoExisteException {
        Pedido pedido = obtenerPedido(idPedido);
        pedido.setCalificacionRestaurante(calificacion);
        pedidoRepository.save(pedido);
    }

    public void modificarCalificacionClientePedido(Long idPedido, DtCalificacion calificacion) throws PedidoNoExisteException {
        Pedido pedido = obtenerPedido(idPedido);
        pedido.setCalificacionCliente(calificacion);
        pedidoRepository.save(pedido);
    }

    public Pedido obtenerPedido(Long id) throws PedidoNoExisteException {
        Pedido pedido = pedidoRepository.findPedidoById(id);
        if (pedido == null) {
            throw new PedidoNoExisteException("No existe pedido con id " + id);
        }
        return pedido;
    }

    public JsonObject buscarPedidoById(Long id) throws PedidoNoExisteException {
        return pedidoConverter.jsonPedido(obtenerPedido(id));
    }

    public List<Pedido> pedidosRestaurante(Restaurante restaurante){
        return pedidoRepository.findPedidosByRestaurante(restaurante);
    }

    public List<Pedido> pedidosRestauranteMedioPago(Restaurante restaurante, MedioPago medioPago) {
        return pedidoRepository.findPedidosByRestauranteAndMedioPago(restaurante,medioPago);
    }

    public List<Pedido> pedidosRestauranteFechaHoraProcesado(Restaurante restaurante, LocalDateTime fechaIni, LocalDateTime fechaFin) {
        return pedidoRepository.findPedidosByRestauranteAndFechaHoraProcesadoBetween(restaurante,fechaIni,fechaFin);
    }

    public List<Pedido> pedidosRestauranteMedioPagoFechaHoraProcesado(Restaurante restaurante, MedioPago medioPago, LocalDateTime fechaIni, LocalDateTime fechaFin) {
        return pedidoRepository.findPedidosByRestauranteAndMedioPagoAndFechaHoraProcesadoBetween(restaurante,medioPago,fechaIni,fechaFin);
    }

}
