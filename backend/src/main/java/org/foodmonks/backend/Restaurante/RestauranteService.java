package org.foodmonks.backend.Restaurante;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.tomcat.jni.Local;
import org.foodmonks.backend.Cliente.Exceptions.ClienteDireccionException;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.Menu.Exceptions.MenuMultiplicadorException;
import org.foodmonks.backend.Menu.Exceptions.MenuNombreExistente;
import org.foodmonks.backend.Menu.Exceptions.MenuPrecioException;
import org.foodmonks.backend.Menu.MenuService;
import org.foodmonks.backend.Pedido.Exceptions.PedidoFechaProcesadoException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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

    @Autowired
    public RestauranteService(
        RestauranteRepository restauranteRepository, 
        PasswordEncoder passwordEncoder, 
        UsuarioRepository usuarioRepository,
        MenuService menuService, 
        RestauranteConverter restauranteConverter, 
        PedidoService pedidoService,
        ReclamoConverter reclamoConverter) {
            this.restauranteRepository = restauranteRepository; 
            this.passwordEncoder = passwordEncoder; 
            this.usuarioRepository = usuarioRepository; 
            this.menuService = menuService; 
            this.restauranteConverter = restauranteConverter;
            this.pedidoService = pedidoService;
            this.reclamoConverter = reclamoConverter;
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
        Restaurante restaurante = new Restaurante(nombre,apellido,correo, passwordEncoder.encode(password),now,calificacion,nombreRestaurante,Integer.valueOf(rut),direccion,pendiente,Integer.valueOf(telefono),descripcion,cuentaPaypal,url);
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

        String[] _order = (!orden.isEmpty() && orden.contains(",")) ? orden.split(",") : null;
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
        if (!correoCliente.isBlank()){
            reclamos = obtenerReclamoCliente(restaurante,correoCliente);
        }
        if (!razon.isBlank()){
            reclamos = obtenerReclamoRazon(restaurante,razon);
        } else {
            reclamos = restaurante.getReclamos();
        }
        if (orden) {
            reclamos.sort(Comparator.comparing(Reclamo::getFecha).reversed());
        }
        return reclamoConverter.arrayJsonReclamo(reclamos);
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

/*    balance = {
        "meses": [
        {
            "mes": "Enero",
            "año": "2021",
            "indicadores": [
                {"ventas efectivo": "55412", "cantidad": "544"},
                {"ventas paypal": "44587", "cantidad": "5578"},
                {"devoluciones efectivo": "4478", "cantidad": "558"},
                {"devoluciones paypal": "4557", "cantidad" : "44"}
            ],
            "subtotal": "-4577"
        },
        {
            "mes": "Febrero",
            "año": "2021",
            "indicadores": [
                {"ventas efectivo": "55412", "cantidad": "544"},
                {"ventas paypal": "44587", "cantidad": "5578"},
                {"devoluciones efectivo": "4478", "cantidad": "558"},
                {"devoluciones paypal": "4557", "cantidad" : "44"}
            ],
            "subtotal": "47552"
        }
    ],
        "totales":  [
            {"ventas efectivo": "55412", "cantidad": "544"},
            {"ventas paypal": "44587", "cantidad": "5578"},
            {"devoluciones efectivo": "4478", "cantidad": "558"},
            {"devoluciones paypal": "4557", "cantidad" : "44"}
        ]
    }*/

    public JsonObject obtenerBalance(String correoRestaurante,String medioPago, String fechaIni, String fechaFin,String estadoPedido) throws RestauranteNoEncontradoException, PedidoFechaProcesadoException {

        Restaurante restaurante = obtenerRestaurante(correoRestaurante);

        JsonObject balance = new JsonObject();
        JsonArray meses = new JsonArray();
        JsonArray totales = new JsonArray();


        float totalVentasEfectivo = 0;
        int totalCantidadVentasEfectivo = 0;
        float totalVentasPaypal = 0;
        int totalCantidadVentasPayPal = 0;
        float totalDevolucionesEfectivo = 0;
        int totalCantidadDevolucionesEfectivo = 0;
        float totalDevolucionesPayPal = 0;
        int totalCantidadDevolucionesPayPal = 0;
        String mes = "";
        int anio = 0;
        // Preparo

        // Estado Pedido
        EstadoPedido estado = null;
        if (estadoPedido!= null && !estadoPedido.isBlank()) {
           estado = EstadoPedido.valueOf(estadoPedido.trim().toUpperCase(Locale.ROOT));
        }

        // Medio Pago
        MedioPago pago = null;
        if (medioPago!= null && !medioPago.isBlank()) {
            pago = MedioPago.valueOf(medioPago.trim().toUpperCase(Locale.ROOT));
        }

        // Fecha
        LocalDateTime fechaInicial = null;
        LocalDateTime fechaFinal = null;

        if (fechaIni != null && !fechaIni.isBlank()){
            fechaInicial = LocalDateTime.parse(fechaIni);
        }

        if (fechaFin != null && !fechaFin.isBlank()){
            fechaFinal = LocalDateTime.parse(fechaFin);
        }
        // Diferencia entre mes de fecha inicio y mes de fecha final
       //  int cantidadMeses = 12;
       //  ver si funciona cantidadMeses
        List<Pedido> listaPedidos;
        if (estado != null && pago !=null && fechaInicial != null && fechaFinal != null) {
            listaPedidos = pedidoService.pedidosRestauranteEstadoMedioPagoFechaHoraProcesado(restaurante,estado,pago,fechaInicial,fechaFinal);
        } else if (estado !=null && pago != null){
            listaPedidos = pedidoService.pedidosRestauranteEstadoMedioPago(restaurante,estado,pago);
        } else if (estado != null && fechaInicial != null && fechaFinal != null){
            listaPedidos = pedidoService.pedidosRestauranteEstadoFechaHoraProcesado(restaurante,estado,fechaInicial,fechaFinal);
        } else if (pago != null && fechaInicial != null && fechaFinal != null){
            listaPedidos = pedidoService.pedidosRestauranteMedioPagoFechaHoraProcesado(restaurante,pago,fechaInicial,fechaFinal);
        } else if (estado != null) {
            listaPedidos = pedidoService.pedidosRestauranteEstado(restaurante,estado);
        } else if (pago != null){
            listaPedidos = pedidoService.pedidosRestauranteMedioPago(restaurante,pago);
        } else if (fechaInicial != null && fechaFinal != null){
            listaPedidos = pedidoService.pedidosRestauranteFechaHoraProcesado(restaurante,fechaInicial,fechaFinal);
        } else {
            listaPedidos = pedidoService.pedidosRestaurante(restaurante);
        }

        if (fechaInicial == null || fechaFinal == null){
            fechaInicial = LocalDateTime.of(LocalDateTime.now().getYear(),LocalDateTime.now().getMonthValue(),1,0,0,0);
            fechaFinal = LocalDateTime.now();
        }

        List <Pedido> aux;
/*        int cantidadMeses = (int) ChronoUnit.MONTHS.between(LocalDate.parse(fechaFinal[0].toString()),LocalDate.parse(fechaFinal[1].toString()));
        cantidadMeses = cantidadMeses + fechaFinal[0].getMonthValue();*/

        for(int i =fechaInicial.getMonthValue(); i <= fechaFinal.getMonthValue(); i++){

            float subTotal = 0;
            aux = listaPedidos;
            float ventasEfectivo = 0;
            int cantidadVentasEfectivo = 0;
            float ventasPaypal = 0;
            int cantidadVentasPayPal = 0;
            float devolucionesEfectivo = 0;
            int cantidadDevolucionesEfectivo = 0;
            float devolucionesPayPal = 0;
            int cantidadDevolucionesPayPal = 0;


            for (Pedido pedido : aux){
                LocalDateTime fechapedido = pedido.getFechaHoraProcesado();
                if (fechapedido == null) {
                    throw new PedidoFechaProcesadoException("Existe un pedido sin fecha de procesado en estado Finalizado-Devuelto-ReclamoRechazado");
                }
                if (pedido.getEstado().equals(EstadoPedido.FINALIZADO) || (pedido.getEstado().equals(EstadoPedido.RECLAMORECHAZADO))) {
                    if ((fechapedido.isEqual(fechaInicial) || fechapedido.isAfter(fechaInicial)) && fechapedido.getMonthValue() == i &&
                            ( fechapedido.isBefore(fechaFinal) || fechapedido.isEqual(fechaFinal))) {
                        if (pedido.getMedioPago().equals(MedioPago.EFECTIVO)) {
                            ventasEfectivo += pedido.getTotal();
                            cantidadVentasEfectivo++;
                            subTotal += pedido.getTotal();
                        } else if (pedido.getMedioPago().equals(MedioPago.PAYPAL)) {
                            ventasPaypal += pedido.getTotal();
                            cantidadVentasPayPal++;
                            subTotal += pedido.getTotal();
                        }
                        mes = pedido.getFechaHoraProcesado().getMonth().toString();
                        anio = pedido.getFechaHoraProcesado().getYear();
                    }
                } else if (pedido.getEstado().equals(EstadoPedido.DEVUELTO)){
                    if ((fechapedido.isEqual(fechaInicial) || fechapedido.isAfter(fechaInicial)) && fechapedido.getMonthValue() == i &&
                            ( fechapedido.isBefore(fechaFinal) || fechapedido.isEqual(fechaFinal))) {
                        if (pedido.getMedioPago().equals(MedioPago.EFECTIVO)) {
                            devolucionesEfectivo += pedido.getTotal();
                            cantidadDevolucionesEfectivo++;
                            subTotal -= pedido.getTotal();
                        } else if (pedido.getMedioPago().equals(MedioPago.PAYPAL)) {
                            devolucionesPayPal += pedido.getTotal();
                            cantidadDevolucionesPayPal++;
                            subTotal -= pedido.getTotal();
                        }
                    }
                }
            }

            totalVentasEfectivo += ventasEfectivo;
            totalCantidadVentasEfectivo += cantidadVentasEfectivo;
            totalVentasPaypal += ventasPaypal;
            totalCantidadVentasPayPal += cantidadVentasPayPal;
            totalDevolucionesEfectivo += devolucionesEfectivo;
            totalCantidadDevolucionesEfectivo += cantidadDevolucionesEfectivo;
            totalDevolucionesPayPal += devolucionesPayPal;
            totalCantidadDevolucionesPayPal += cantidadDevolucionesPayPal;

            JsonObject jsonMes = new JsonObject();
            jsonMes.addProperty("mes", mes);
            jsonMes.addProperty("anio", anio);
            JsonArray indicadores = new JsonArray();

            // Cargar los 4 json de indicadores
            JsonObject jsonIndicadorVE = new JsonObject();
            jsonIndicadorVE.addProperty("ventas efectivo", ventasEfectivo);
            jsonIndicadorVE.addProperty("cantidad", cantidadVentasEfectivo);

            JsonObject jsonIndicadorVPP = new JsonObject();
            jsonIndicadorVPP.addProperty("ventas paypal", ventasPaypal);
            jsonIndicadorVPP.addProperty("cantidad", cantidadVentasPayPal);

            JsonObject jsonIndicadorDE = new JsonObject();
            jsonIndicadorDE.addProperty("devoluciones efectivo", devolucionesEfectivo);
            jsonIndicadorDE.addProperty("cantidad", cantidadDevolucionesEfectivo);

            JsonObject jsonIndicadorDPP = new JsonObject();
            jsonIndicadorDPP.addProperty("devoluciones paypal", devolucionesPayPal);
            jsonIndicadorDPP.addProperty("cantidad", cantidadDevolucionesPayPal);

            indicadores.add(jsonIndicadorVE);
            indicadores.add(jsonIndicadorVPP);
            indicadores.add(jsonIndicadorDE);
            indicadores.add(jsonIndicadorDPP);
            jsonMes.add("indicadores", indicadores);
            jsonMes.addProperty("subtotal", subTotal);

            // Termina iteracion de todo lo relacionado a un mes
            meses.add(jsonMes);
        }

        // Cargo totales

        JsonObject ventasEfectivoTotal = new JsonObject();
        ventasEfectivoTotal.addProperty("ventasEfectivo", totalVentasEfectivo);
        ventasEfectivoTotal.addProperty("cantidad", totalCantidadVentasEfectivo);

        JsonObject ventasPayPalTotal = new JsonObject();
        ventasPayPalTotal.addProperty("ventasPayPal", totalVentasPaypal);
        ventasPayPalTotal.addProperty("cantidad", totalCantidadVentasPayPal);

        JsonObject devolucionesEfectivoTotal = new JsonObject();
        devolucionesEfectivoTotal.addProperty("devolucionesEfectivo", totalDevolucionesEfectivo);
        devolucionesEfectivoTotal.addProperty("cantidad", totalCantidadDevolucionesEfectivo);

        JsonObject devolucionesPayPalTotal = new JsonObject();
        devolucionesPayPalTotal.addProperty("devolucionesPayPal", totalDevolucionesPayPal);
        devolucionesPayPalTotal.addProperty("cantidad", totalCantidadDevolucionesPayPal);

        // Termina todo, retorno balance
        totales.add(ventasEfectivoTotal);
        totales.add(ventasPayPalTotal);
        totales.add(devolucionesEfectivoTotal);
        totales.add(devolucionesPayPalTotal);
        balance.add("meses", meses);
        balance.add("totales", totales);
        return balance;
    }

}
