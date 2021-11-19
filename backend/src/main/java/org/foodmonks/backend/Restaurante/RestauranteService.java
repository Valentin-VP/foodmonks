package org.foodmonks.backend.Restaurante;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.foodmonks.backend.Cliente.Exceptions.ClienteDireccionException;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.EmailService.EmailNoEnviadoException;
import org.foodmonks.backend.EmailService.EmailService;
import org.foodmonks.backend.Menu.Exceptions.MenuIdException;
import org.foodmonks.backend.Menu.Exceptions.MenuMultiplicadorException;
import org.foodmonks.backend.Menu.Exceptions.MenuNombreExistente;
import org.foodmonks.backend.Menu.Exceptions.MenuPrecioException;
import org.foodmonks.backend.Menu.MenuService;
import org.foodmonks.backend.Pedido.Exceptions.PedidoDevolucionException;
import org.foodmonks.backend.Pedido.Exceptions.PedidoDistintoRestauranteException;
import org.foodmonks.backend.Pedido.Exceptions.PedidoIdException;
import org.foodmonks.backend.Pedido.Exceptions.PedidoNoExisteException;
import org.foodmonks.backend.Pedido.Pedido;
import org.foodmonks.backend.Pedido.PedidoService;
import org.foodmonks.backend.Reclamo.Reclamo;
import org.foodmonks.backend.Reclamo.ReclamoConverter;
import org.foodmonks.backend.Restaurante.Exceptions.RestauranteFaltaMenuException;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioExisteException;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioNoRestaurante;
import org.foodmonks.backend.Usuario.UsuarioRepository;
import org.foodmonks.backend.datatypes.CategoriaMenu;
import org.foodmonks.backend.Restaurante.Exceptions.RestauranteNoEncontradoException;
import org.foodmonks.backend.datatypes.EstadoPedido;
import org.foodmonks.backend.datatypes.EstadoRestaurante;
import org.foodmonks.backend.datatypes.MedioPago;
import org.foodmonks.backend.paypal.PayPalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class RestauranteService {

    private final PasswordEncoder passwordEncoder;
    private final RestauranteRepository restauranteRepository;
    private final UsuarioRepository usuarioRepository;
    private final MenuService menuService;
    private final RestauranteConverter restauranteConverter;
    private final PedidoService pedidoService;
    private final ReclamoConverter reclamoConverter;
    private final TemplateEngine templateEngine;
    private final EmailService emailService;
    private final PayPalService payPalService;

    @Autowired
    public RestauranteService(
        RestauranteRepository restauranteRepository,
        PasswordEncoder passwordEncoder,
        UsuarioRepository usuarioRepository,
        MenuService menuService, 
        RestauranteConverter restauranteConverter, 
        PedidoService pedidoService,
        ReclamoConverter reclamoConverter,
        TemplateEngine templateEngine,
        EmailService emailService,
        PayPalService payPalService) {
            this.restauranteRepository = restauranteRepository; 
            this.passwordEncoder = passwordEncoder; 
            this.usuarioRepository = usuarioRepository; 
            this.menuService = menuService; 
            this.restauranteConverter = restauranteConverter;
            this.pedidoService = pedidoService;
            this.reclamoConverter = reclamoConverter;
            this.templateEngine = templateEngine;
            this.emailService = emailService;
            this.payPalService = payPalService;
    }

    public List<Restaurante> listarRestaurante(){
        return restauranteRepository.findAll();
    }

    public Restaurante buscarRestaurante(String correo) {
        return restauranteRepository.findByCorreo(correo);
    }

    public void editarRestaurante(Restaurante restaurante) {
        restauranteRepository.save(restaurante);
    }

    public void modificarEstado(String correo, EstadoRestaurante estado) {
        Restaurante restauranteAux = restauranteRepository.findByCorreo(correo);
        restauranteAux.setEstado(estado);
        restauranteRepository.save(restauranteAux);
    }

    public void createSolicitudAltaRestaurante(String nombre, String apellido, String correo, String password, LocalDate now, float calificacion, String nombreRestaurante, String rut, Direccion direccion, EstadoRestaurante pendiente, String telefono, String descripcion, String cuentaPaypal, String url,ArrayList<JsonObject> jsonMenus) throws UsuarioExisteException, ClienteDireccionException, RestauranteFaltaMenuException, UsuarioNoRestaurante, MenuNombreExistente, MenuPrecioException, MenuMultiplicadorException {
        if (usuarioRepository.findByCorreo(correo) != null) {
            throw new UsuarioExisteException("Ya existe un Usuario registrado con el correo " + correo);
        }
        if (direccion == null){
            throw new ClienteDireccionException("Debe ingresar una direccion");
        }
        if (jsonMenus.size() < 3){
            throw new RestauranteFaltaMenuException("Debe ingresar al menos 3 menus");
        }
        Restaurante restaurante = new Restaurante(nombre,apellido,correo, passwordEncoder.encode(password),now,calificacion,nombreRestaurante,Long.valueOf(rut),direccion,pendiente,Integer.valueOf(telefono),descripcion,cuentaPaypal,url);
        restauranteRepository.save(restaurante);
        for (JsonObject menu: jsonMenus){
            menuService.altaMenu(menu,restaurante.getCorreo());
        }
    }

    public EstadoRestaurante restauranteEstado (String correo) throws RestauranteNoEncontradoException {
        Restaurante restaurante = restauranteRepository.findByCorreo(correo);
        if (restaurante == null) {
            throw new RestauranteNoEncontradoException("No existe el Restaurante " + correo);
        }
        return restaurante.getEstado();
    }

    public JsonObject obtenerJsonRestaurante(String correo) throws RestauranteNoEncontradoException {
        Restaurante restaurante = restauranteRepository.findByCorreo(correo);
        if (restaurante == null) {
            throw new RestauranteNoEncontradoException("No existe el Restaurante " + correo);
        }
        return restauranteConverter.jsonRestaurante(restaurante);

    }
  
    public void registrarPagoEfectivo(String correo, Long idPedido) throws RestauranteNoEncontradoException, PedidoNoExisteException {
        Restaurante restaurante = restauranteRepository.findByCorreo(correo);
        if (restaurante == null) {
            throw new RestauranteNoEncontradoException("No existe el Restaurante " + correo);
        }
        if (!pedidoService.existePedido(idPedido)){
            throw new PedidoNoExisteException("No existe el pedido con id "+ idPedido);
        }
        if (!pedidoService.existePedidoRestaurante(idPedido,restaurante)){
            throw new RestauranteNoEncontradoException("No existe el pedido con id " + idPedido + " para el Restaurante " + correo);
        }
        pedidoService.cambiarEstadoPedido(idPedido, EstadoPedido.FINALIZADO);
      }
  
  
    public List<JsonObject> listarPedidosEfectivoConfirmados(String correo) throws RestauranteNoEncontradoException {
        Restaurante restaurante = restauranteRepository.findByCorreo(correo);
        if (restaurante == null){
            throw new RestauranteNoEncontradoException("No existe el Restaurante " + correo);
        }
        //System.out.println(restaurante.getNombreRestaurante() + restaurante.getPedidos());
        return pedidoService.listaPedidosEfectivoConfirmados(restaurante);
    }
  
  
    public List<JsonObject> listaRestaurantesAbiertos(String nombreRestaurante, String categoriaMenu, boolean ordenCalificacion){

        if (ordenCalificacion) {
            if (!nombreRestaurante.isBlank()){
                return restauranteConverter.listaRestaurantes(restauranteRepository.findRestaurantesByNombreRestauranteContainsAndEstadoOrderByCalificacionDesc(nombreRestaurante,EstadoRestaurante.ABIERTO));
            }
            if (!categoriaMenu.isBlank()) {
                List<Restaurante> restaurantes = restauranteRepository.findRestaurantesByEstadoOrderByCalificacionDesc(EstadoRestaurante.ABIERTO);
                CategoriaMenu categoria = CategoriaMenu.valueOf(categoriaMenu);
                return obtenerRestauranteMenuConCategoria(restaurantes,categoria);
            }
            return  restauranteConverter.listaRestaurantes(restauranteRepository.findRestaurantesByEstadoOrderByCalificacionDesc(EstadoRestaurante.ABIERTO));
        } else {
            if (!nombreRestaurante.isBlank()){
                return restauranteConverter.listaRestaurantes(restauranteRepository.findRestaurantesByNombreRestauranteContainsAndEstado(nombreRestaurante,EstadoRestaurante.ABIERTO));
            }
            if (!categoriaMenu.isBlank()) {
                List<Restaurante> restaurantes = restauranteRepository.findRestaurantesByEstado(EstadoRestaurante.ABIERTO);
                CategoriaMenu categoria = CategoriaMenu.valueOf(categoriaMenu);
                return obtenerRestauranteMenuConCategoria(restaurantes,categoria);
            }
            return  restauranteConverter.listaRestaurantes(restauranteRepository.findRestaurantesByEstado(EstadoRestaurante.ABIERTO));
        }
    }

    public List<JsonObject> obtenerRestauranteMenuConCategoria(List<Restaurante> restaurantes, CategoriaMenu categoriaMenu){
        List<Restaurante> result = new ArrayList<>();
        for (Restaurante restaurante : restaurantes) {
            if (menuService.existeCategoriaMenu(restaurante, categoriaMenu)) {
                result.add(restaurante);
            }
        }
        return restauranteConverter.listaRestaurantes(result);
    }

    //Si el estado es "CONFIRMADO" se agregan el tiempo a fechaHoraEntrega (a partir de fechaHoraProcesado)
    public void actualizarEstadoPedido(String correo, Long idPedido, String estado, Integer minutos) throws RestauranteNoEncontradoException, PedidoNoExisteException {
        Restaurante restaurante = restauranteRepository.findByCorreo(correo);
        if (restaurante == null) {
            throw new RestauranteNoEncontradoException("No existe el Restaurante " + correo);
        }
        if (!pedidoService.existePedidoRestaurante(idPedido,restaurante)){
            throw new RestauranteNoEncontradoException("No existe el pedido con id " + idPedido + " para el Restaurante " + correo);
        }
        Pedido pedido = pedidoService.obtenerPedido(idPedido);

        if (estado.equals("CONFIRMADO")){
            pedidoService.cambiarFechasEntregaProcesado(idPedido, minutos);
            if (pedido.getMedioPago().equals(MedioPago.EFECTIVO)){
                pedidoService.cambiarEstadoPedido(idPedido, EstadoPedido.CONFIRMADO);
            }else{
                pedidoService.cambiarEstadoPedido(idPedido, EstadoPedido.FINALIZADO);
            }
        }else if (estado.equals("RECHAZADO")){
            pedidoService.cambiarEstadoPedido(idPedido, EstadoPedido.RECHAZADO);
            if (pedido.getMedioPago().equals(MedioPago.PAYPAL)){
                // HACER DEVOLUCION DE PAYPAL
            }
        }
    }
  
  
    public Restaurante obtenerRestaurante(String correo) throws RestauranteNoEncontradoException {
        Restaurante restaurante = restauranteRepository.findByCorreo(correo);
        if (restaurante == null) {
            throw new RestauranteNoEncontradoException("No existe el Restaurante " + correo);
        }
      return restaurante;
    }

    public List<JsonObject> listarPedidosPendientes(String correo) throws RestauranteNoEncontradoException {
        Restaurante restaurante = restauranteRepository.findByCorreo(correo);
        if (restaurante==null) {
            throw new RestauranteNoEncontradoException("No existe el Restaurante " + correo);
        }
        return pedidoService.listaPedidosPendientes(restaurante);
    }

    public JsonObject listarHistoricoPedidos(String correo, String estadoPedido, String medioPago, String orden, String fecha, String total, String page, String size) throws RestauranteNoEncontradoException {
        Restaurante restaurante = restauranteRepository.findByCorreo(correo);
        if (restaurante==null) {
            throw new RestauranteNoEncontradoException("No existe el Restaurante " + correo);
        }
        String[] _total = (total!=null && total.contains(",")) ? total.split(",") : null;
        String[] _fecha = (fecha!=null && fecha.contains(",")) ? fecha.split(",") : null;
        //String[] _order = (!orden.isEmpty() && orden.contains(",")) ? orden.split(",") : null;
        Float[] totalFinal = new Float[2];
        LocalDateTime[] fechaFinal = new LocalDateTime[2];

        EstadoPedido estado = null;
        MedioPago pago = null;
        int pageFinal = 0;
        int sizeFinal = 10;
        if (estadoPedido!= null && !estadoPedido.equals("")) {
            try {
                estado = EstadoPedido.valueOf(estadoPedido.trim().toUpperCase(Locale.ROOT));
            }catch(IllegalArgumentException e){
                estado = null;
            }
        }
        if (medioPago!= null && !medioPago.equals("")) {
            try{
                pago = MedioPago.valueOf(medioPago.trim().toUpperCase(Locale.ROOT));
            }catch(IllegalArgumentException e){
                pago = null;
            }
        }

        if (_total != null && _total[0] != null && _total[1] != null){
            try{
                totalFinal[0] = Math.abs(Float.valueOf(_total[0]));
                totalFinal[1] = Math.abs(Float.valueOf(_total[1]));
            }catch(NumberFormatException e){
                totalFinal = null;
            }
        }else
            totalFinal = null;

        if (_fecha != null && _fecha[0] != null && _fecha[1] != null){
            try{
                fechaFinal[0] = LocalDateTime.of(LocalDate.parse(_fecha[0], DateTimeFormatter.ofPattern("yyyy-MM-dd")), LocalTime.MIDNIGHT);
                fechaFinal[1] = LocalDateTime.of(LocalDate.parse(_fecha[1], DateTimeFormatter.ofPattern("yyyy-MM-dd")), LocalTime.MIDNIGHT);
            }catch(DateTimeException e){
                fechaFinal = null;
            }
        }else
            fechaFinal = null;

        try{
            pageFinal = Integer.parseInt(page);
            sizeFinal = Integer.parseInt(size);
        }catch(NumberFormatException e){
            e.printStackTrace();
            pageFinal = 0;
            sizeFinal = 5;
        }
        return pedidoService.listaPedidosHistorico(restaurante, estado, pago, orden, fechaFinal, totalFinal, pageFinal, sizeFinal);
    }

    public void agregarReclamoRestaurante(Restaurante restaurante, Reclamo reclamo){
        List<Reclamo> reclamos = restaurante.getReclamos();
        reclamos.add(reclamo);
        restauranteRepository.save(restaurante);
    }

    public JsonArray listarReclamos(String correoRestaurante, boolean orden, String correoCliente, String razon) throws RestauranteNoEncontradoException {
        Restaurante restaurante = obtenerRestaurante(correoRestaurante);
        List<Reclamo> reclamos;
        if (!correoCliente.isBlank() && !correoCliente.isBlank()) {
            reclamos = obtenerReclamoClienteRazon(restaurante,correoCliente,razon);
        } else if (!correoCliente.isBlank()){
            reclamos = obtenerReclamoCliente(restaurante,correoCliente);
        } else if (!razon.isBlank()){
            reclamos = obtenerReclamoRazon(restaurante,razon);
        } else {
            reclamos = restaurante.getReclamos();
        }
        if (orden) {
            reclamos.sort(Comparator.comparing(Reclamo::getFecha).reversed());
        }
        return reclamoConverter.arrayJsonReclamo(reclamos);
    }

    public List<Reclamo> obtenerReclamoClienteRazon (Restaurante restaurante, String correoCliente, String razon){
        List<Reclamo> reclamos = new ArrayList<>();
        for (Reclamo reclamo : restaurante.getReclamos()){
            if (reclamo.getPedido() != null && reclamo.getPedido().getCliente() != null && !reclamo.getPedido().getCliente().getCorreo().isBlank()){
                if (reclamo.getPedido().getCliente().getCorreo().contains(correoCliente) && !reclamo.getRazon().isBlank() && reclamo.getRazon().contains(razon)){
                    reclamos.add(reclamo);
                }
            }
        }
        return  reclamos;
    }

    public List<Reclamo> obtenerReclamoCliente (Restaurante restaurante, String correoCliente){
        List<Reclamo> reclamos = new ArrayList<>();
        for (Reclamo reclamo : restaurante.getReclamos()){
            if (reclamo.getPedido() != null && reclamo.getPedido().getCliente() != null && !reclamo.getPedido().getCliente().getCorreo().isBlank()){
                if (reclamo.getPedido().getCliente().getCorreo().contains(correoCliente)){
                    reclamos.add(reclamo);
                }
            }
        }
        return reclamos;
    }

    public List<Reclamo> obtenerReclamoRazon (Restaurante restaurante, String razon){
        List<Reclamo> reclamos = new ArrayList<>();
        for (Reclamo reclamo : restaurante.getReclamos()){
            if (!reclamo.getRazon().isBlank() && reclamo.getRazon().contains(razon)){
                reclamos.add(reclamo);
            }
        }
        return reclamos;
    }

    public JsonObject realizarDevolucion(String correoRestaurante, String idPedido, String motivoDevolucion, Boolean estadoDevolucion) throws PedidoNoExisteException, PedidoIdException, RestauranteNoEncontradoException, EmailNoEnviadoException, IOException, PedidoDevolucionException, PedidoDistintoRestauranteException {
        Restaurante restaurante = obtenerRestaurante(correoRestaurante);
        JsonObject response = new JsonObject();
        response.addProperty("Mensaje", "Devolucion");
        if (!idPedido.matches("[0-9]*") || idPedido.isBlank()){
            throw new PedidoIdException("El id del pedido no es un numero entero");
        }
        Pedido pedido = pedidoService.obtenerPedido(Long.valueOf(idPedido));
        if (!pedido.getEstado().equals(EstadoPedido.FINALIZADO)){
            throw new PedidoDevolucionException("El pedido no esta FINALIZADO, no se puede aplicar una devolucion");
        }
        if (!pedido.getRestaurante().getCorreo().equals(restaurante.getCorreo())){
            throw new PedidoDistintoRestauranteException("El pedido id "+ idPedido + " no pertenece al restaurante " + correoRestaurante);
        }
        Context context = new Context();
        context.setVariable("user", pedido.getCliente().getNombre() + " " + pedido.getCliente().getApellido());
        String[] cc = new String[1];
        cc[0] = pedido.getRestaurante().getCorreo();
        String htmlContent = "";
        if (estadoDevolucion) {
            if (pedido.getMedioPago().equals(MedioPago.EFECTIVO)){
                pedidoService.cambiarEstadoPedido(pedido.getId(),EstadoPedido.DEVUELTO);
                response.addProperty("status","completado");
                context.setVariable("contenido", "Su reclamo al restaurante " + restaurante.getNombreRestaurante() + " ha sido aceptado.");
                context.setVariable("pedido", "Identificador pedido: #" + pedido.getId());
                context.setVariable("monto", "Monto: " + pedido.getTotal().toString());
                context.setVariable("mensaje", "Para la devolución deberá contactar con el restaurante por alguno de los siguientes medios:");
                context.setVariable("direccion", "Direccion: " + restaurante.getDireccion().getCalle() + " " +
                        restaurante.getDireccion().getNumero() + ", esquina " + restaurante.getDireccion().getEsquina());
                context.setVariable("telefono", "Telefono: " + restaurante.getTelefono().toString());
                context.setVariable("email", "Email: " + restaurante.getCorreo());
                htmlContent = templateEngine.process("reclamo-aceptado-efectivo", context);
            } else if (pedido.getMedioPago().equals(MedioPago.PAYPAL)){
                response.addProperty("status", payPalService.refundOrder(payPalService.getOrder(pedido.getOrdenPaypal().getOrdenId())));
                context.setVariable("contenido", "Su reclamo al restaurante " + restaurante.getNombreRestaurante() + " ha sido aceptado.");
                context.setVariable("pedido", "Identificador pedido: #" + pedido.getId());
                context.setVariable("paypal", "Identificador Paypal: order#" + pedido.getOrdenPaypal().getOrdenId());
                context.setVariable("mensaje", "Te hemos realizado una devolucion de $" + pedido.getTotal() + " en tu cuenta de Paypal.");
                htmlContent = templateEngine.process("reclamo-aceptado-paypal", context);
            }
            emailService.enviarMail(pedido.getCliente().getCorreo(), "Reclamo aceptado :" + pedido.getReclamo().getRazon(), htmlContent,cc);
            pedidoService.cambiarEstadoPedido(pedido.getId(),EstadoPedido.DEVUELTO);
        } else {
            if (pedido.getMedioPago().equals(MedioPago.PAYPAL)) {
                context.setVariable("contenido","Su reclamo al restaurante " + restaurante.getNombreRestaurante() + ", ha sido rechazado.");
                context.setVariable("pedido","Identificador pedido: #" + pedido.getId());
                context.setVariable("paypal", "Identificador Paypal order#" + pedido.getOrdenPaypal().getOrdenId());
                context.setVariable("motivo",motivoDevolucion);
                htmlContent = templateEngine.process("reclamo-rechazado-paypal", context);
            } else if (pedido.getMedioPago().equals(MedioPago.EFECTIVO)){
                context.setVariable("contenido","Su reclamo al restaurante " + restaurante.getNombreRestaurante() + ", ha sido rechazado.");
                context.setVariable("pedido","Identificador pedido: #" + pedido.getId());
                context.setVariable("motivo",motivoDevolucion);
                htmlContent = templateEngine.process("reclamo-rechazado-efectivo", context);
            }
            emailService.enviarMail(pedido.getCliente().getCorreo(), "Reclamo rechazo: " + pedido.getReclamo().getRazon(), htmlContent,cc);
            response.addProperty("status","Mail de rechazo enviado");
            pedidoService.cambiarEstadoPedido(pedido.getId(),EstadoPedido.RECLAMORECHAZADO);
        }
        return response;
    }

    public JsonObject pedidosRegistrados(int anio){
        JsonObject result = new JsonObject();
        List<Restaurante> restaurantes = restauranteRepository.findAllByRolesOrderByCalificacion("ROLE_RESTAURANTE");
        for (int i=1; i <= 12 ; i++) {
            LocalDateTime fechaIni = LocalDateTime.of(LocalDate.of(anio, i, 1),LocalTime.MIDNIGHT);
            LocalDateTime fechaFin;
            if (i == 12) {
                fechaFin = LocalDateTime.of(LocalDate.of(anio + 1, 1, 1).minusDays(1),LocalTime.MAX);
            } else {
                fechaFin = LocalDateTime.of(LocalDate.of(anio, i + 1, 1).minusDays(1),LocalTime.MAX);
            }
            JsonArray mes = new JsonArray();
            for (Restaurante restaurante : restaurantes){
                JsonObject pedidosRestaurante = new JsonObject();
                pedidosRestaurante.addProperty("restaurante",restaurante.getNombreRestaurante());
                pedidosRestaurante.addProperty("pedidosRegistrados", pedidoService.cantPedidosRestaurante(restaurante,fechaIni,fechaFin));
                mes.add(pedidosRestaurante);
            }
            result.add(fechaIni.getMonth().toString(), mes);
        }
        return result;
    }

}
