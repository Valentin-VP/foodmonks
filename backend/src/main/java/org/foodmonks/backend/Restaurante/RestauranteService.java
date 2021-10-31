package org.foodmonks.backend.Restaurante;

import com.google.gson.JsonObject;
import org.foodmonks.backend.Cliente.Exceptions.ClienteDireccionException;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.Menu.Exceptions.MenuNombreExistente;
import org.foodmonks.backend.Menu.MenuService;
import org.foodmonks.backend.Restaurante.Exceptions.RestauranteFaltaMenuException;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioExisteException;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioNoRestaurante;
import org.foodmonks.backend.Usuario.UsuarioRepository;
import org.foodmonks.backend.datatypes.CategoriaMenu;
import org.foodmonks.backend.Restaurante.Exceptions.RestauranteNoEncontradoException;
import org.foodmonks.backend.datatypes.EstadoRestaurante;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RestauranteService {

    private final PasswordEncoder passwordEncoder;
    private final RestauranteRepository restauranteRepository;
    private final UsuarioRepository usuarioRepository;
    private final MenuService menuService;

    @Autowired
    public RestauranteService(RestauranteRepository restauranteRepository, PasswordEncoder passwordEncoder , UsuarioRepository usuarioRepository, MenuService menuService) {
        this.restauranteRepository = restauranteRepository; this.passwordEncoder = passwordEncoder; this.usuarioRepository = usuarioRepository; this.menuService = menuService;
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

    public void modificarEstado(String correo,EstadoRestaurante estado) {
        Restaurante restauranteAux = restauranteRepository.findByCorreo(correo);
        restauranteAux.setEstado(estado);
        restauranteRepository.save(restauranteAux);
    }

    public void createSolicitudAltaRestaurante(String nombre, String apellido, String correo, String password, LocalDate now, float calificacion, String nombreRestaurante, String rut, Direccion direccion, EstadoRestaurante pendiente, String telefono, String descripcion, String cuentaPaypal, String url,ArrayList<JsonObject> jsonMenus) throws UsuarioExisteException, ClienteDireccionException, RestauranteFaltaMenuException, UsuarioNoRestaurante, MenuNombreExistente {
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
            menuService.altaMenu(
                    menu.get("nombre").getAsString(),
                    Float.valueOf(menu.get("price").getAsString()),
                    menu.get("descripcion").getAsString(),
                    true,
                    Float.valueOf(menu.get("multiplicador").getAsString()),
                    menu.get("imagen").getAsString(),
                    CategoriaMenu.valueOf(menu.get("categoria").getAsString()),
                    restaurante.getCorreo());
        }
    }

    public EstadoRestaurante restauranteEstado (String correo) throws RestauranteNoEncontradoException {
        Restaurante restaurante = restauranteRepository.findByCorreo(correo);
        if (restaurante == null) {
            throw new RestauranteNoEncontradoException("No existe el Restaurante " + correo);
        }
        return restaurante.getEstado();
    }

    public List<Restaurante> listaRestaurantesAbiertos(String nombreRestaurante, String categoriaMenu, boolean ordenCalificacion){
        if (!nombreRestaurante.isEmpty()){
            return restauranteRepository.findRestaurantesByNombreAndEstado(nombreRestaurante,EstadoRestaurante.ABIERTO);
        }
        if (ordenCalificacion) {
            return  restauranteRepository.findRestaurantesByEstadoOrderByCalificacion(EstadoRestaurante.ABIERTO);
        }
        if (!categoriaMenu.isEmpty()){
            List<Restaurante> restaurantes = restauranteRepository.findRestaurantesByEstado(EstadoRestaurante.ABIERTO);
            List<Restaurante> result = new ArrayList<>();
            CategoriaMenu categoria = CategoriaMenu.valueOf(categoriaMenu);
            for (Restaurante restaurante : restaurantes){
                if (menuService.existeCategoriaMenu(restaurante,categoria)){
                    result.add(restaurante);
                }
            }
            return result;
        }
        return restauranteRepository.findRestaurantesByEstado(EstadoRestaurante.ABIERTO);
    }

}
