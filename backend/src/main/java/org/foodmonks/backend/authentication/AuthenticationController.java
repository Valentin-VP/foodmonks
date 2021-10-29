package org.foodmonks.backend.authentication;


import com.google.gson.Gson;
import com.google.gson.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.foodmonks.backend.Admin.Admin;
import org.foodmonks.backend.Admin.AdminService;
import org.foodmonks.backend.Cliente.Cliente;
import org.foodmonks.backend.Cliente.ClienteService;
import org.foodmonks.backend.Cliente.Exceptions.ClienteNoEncontradoException;
import org.foodmonks.backend.EmailService.EmailNoEnviadoException;
import org.foodmonks.backend.EmailService.EmailService;
import org.foodmonks.backend.Restaurante.Exceptions.RestauranteNoEncontradoException;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.Restaurante.RestauranteService;
import org.foodmonks.backend.Usuario.UsuarioService;
import org.foodmonks.backend.datatypes.EstadoCliente;
import org.foodmonks.backend.datatypes.EstadoRestaurante;
import org.foodmonks.backend.dynamodb.TokenReset;
import org.foodmonks.backend.dynamodb.TokenResetDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@RestController
@RequestMapping("/api/v1")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    TokenHelper tokenHelper;

    @Autowired
    private UserDetailsService customService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private RestauranteService restauranteService;

    @Autowired
    private Environment env;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenResetDAO awsService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TemplateEngine templateEngine;

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@Parameter @RequestBody AuthenticationRequest authenticationRequest) throws InvalidKeySpecException, NoSuchAlgorithmException {
        final Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getEmail(), authenticationRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails usuario = customService.loadUserByUsername(authenticationRequest.getEmail());

        if(usuario.isEnabled()) {
            String jwtToken = tokenHelper.generateToken(usuario.getUsername(), usuario.getAuthorities());
            String jwtRefreshToken = tokenHelper.generateRefreshToken(usuario.getUsername(), usuario.getAuthorities());

            AuthenticationResponse response = new AuthenticationResponse();
            response.setToken(jwtToken);
            response.setRefreshToken(jwtRefreshToken);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/auth/userinfo")
    @Operation(summary = "Obtiene información del Usuario", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getUserInfo(Authentication user) {

        if (adminService.buscarAdmin(user.getName()) != null) {
            InfoAdmin adminInfo = new InfoAdmin();
            Admin admin = adminService.buscarAdmin(user.getName());
            adminInfo.setRoles(admin.getAuthorities().toArray());
            return new ResponseEntity<>(adminInfo, HttpStatus.OK);

        } else if (restauranteService.buscarRestaurante(user.getName()) != null) {
            InfoRestaurante restauranteInfo = new InfoRestaurante();
            Restaurante restaurante = restauranteService.buscarRestaurante(user.getName());
            restauranteInfo.setNombre(restaurante.getNombreRestaurante());
            restauranteInfo.setDescripcion(restaurante.getDescripcion());
            restauranteInfo.setRoles(restaurante.getAuthorities().toArray());
            return new ResponseEntity<>(restauranteInfo, HttpStatus.OK);

        } else if (clienteService.buscarCliente(user.getName()) != null) {
            InfoCliente clienteInfo = new InfoCliente();
            Cliente cliente = clienteService.buscarCliente(user.getName());
            clienteInfo.setFirstName(cliente.getNombre());
            clienteInfo.setLastName(cliente.getApellido());
            clienteInfo.setRoles(cliente.getAuthorities().toArray());
            return new ResponseEntity<>(clienteInfo, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("no se encontro ningun tipo de usuario", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Solicitud de cambio de contraseña",
            description = "Genera una solicitud de cambio de contraseña, enviando un enlace por correo para el cambio",
            tags = { "usuario", "autenticación" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud realizada con éxito"),
            @ApiResponse(responseCode = "400", description = "Ha ocurrido un error")
    })
    @PostMapping(path = "/password/recuperacion/solicitud")
    public ResponseEntity<?> solicitarCambioPassword(
            @Parameter(description = "Correo del usuario que requiere cambio de password", required = true)
            @RequestBody String data)  {
        /*
        * chequear que existe usuario con correo
        * generar token en back
        * guardar token en Firebase
        * enviar correo
        * retornar un 200 OK
        * */
        try {
            // generar resetToken
            JsonObject jsonReset = new Gson().fromJson(data, JsonObject.class);
            String correo = jsonReset.get("email").getAsString();
            String resetToken = usuarioService.generarTokenResetPassword();
            String nombre = null;
            // Chequear si el usuario existe y esta habilitado
            if (adminService.buscarAdmin(correo) != null) {
                nombre = adminService.buscarAdmin(correo).getNombre();
            } else if (restauranteService.buscarRestaurante(correo) != null) {
                nombre = restauranteService.buscarRestaurante(correo).getNombreRestaurante();
                if (restauranteNoHabilitado(correo)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(String.format("El restaurante %s con correo %s no se encuentra habilitado " +
                                    "para cambiar su password", nombre ,correo));
                }
            } else if (clienteService.buscarCliente(correo) != null) {
                nombre = clienteService.buscarCliente(correo).getNombre();
                if (clienteNoHabilitado(correo)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(String.format("El cliente %s con correo %s no se encuentra habilitado " +
                                    "para cambiar su password", nombre ,correo));
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(String.format("No existe usuario con correo %s", correo));
            }
            TokenReset tokenReset = new TokenReset(correo, resetToken);
            awsService.setToken(tokenReset);
            generarMailResetPassword(correo, nombre, resetToken);
            return ResponseEntity.ok("Solicitud realizada con éxito");
        } catch (EmailNoEnviadoException | ClienteNoEncontradoException | RestauranteNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(String.format("Ha ocurrido un error: %s", e.getMessage()));
        }
    }

    @Operation(summary = "Realización de cambio de contraseña",
            description = "Realiza el cambio de contraseña de un usuario",
            tags = { "usuario", "autenticación" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nueva password cambiada con éxito"),
            @ApiResponse(responseCode = "400", description = "Ha ocurrido un error")
    })
    @PostMapping(path = "/password/recuperacion/cambio")
    public ResponseEntity<?> realizarCambioPassword(
            @Parameter(description = "Nuevos datos para cambio de password", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "application/json"))
            @RequestBody String resetRequest) {
        try{
            JsonObject jsonReset = new Gson().fromJson(resetRequest, JsonObject.class);
            String correo = jsonReset.get("correo").getAsString();
            String resetToken = jsonReset.get("token").getAsString();
            String password = new String(Base64.getDecoder().decode(jsonReset.get("password").getAsString()));
            String nombre = null;
            // Chequear si el usuario existe y esta habilitado
            if (adminService.buscarAdmin(correo) != null) {
                nombre = adminService.buscarAdmin(correo).getNombre();
            } else if (restauranteService.buscarRestaurante(correo) != null) {
                nombre = restauranteService.buscarRestaurante(correo).getNombreRestaurante();
                if (restauranteNoHabilitado(correo)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(String.format("El restaurante %s con correo %s no se encuentra habilitado " +
                                    "para cambiar su password", nombre ,correo));
                }
            } else if (clienteService.buscarCliente(correo) != null) {
                nombre = clienteService.buscarCliente(correo).getNombre();
                if (clienteNoHabilitado(correo)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(String.format("El cliente %s con correo %s no se encuentra habilitado " +
                                    "para cambiar su password", nombre ,correo));
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(String.format("No existe usuario con correo %s", correo));
            }
            // Chequear que token coincida y exista en Firestore - Arroja excepcion si algo falla
            TokenReset tokenReset = new TokenReset(correo, resetToken);
            if (awsService.comprobarResetToken(tokenReset)){
                // Todo ok, cambiar password
                usuarioService.cambiarPassword(correo, password);
                return ResponseEntity.ok("Nueva password cambiada con éxito");
            } else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Los tokens no coinciden");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format("Ha ocurrido un error: %s", e.getMessage()));
        }

    }

    @Operation(summary = "Chequeo previo a cambio de contraseña",
            description = "Valida que el token recibido esté asociado al usuario con correo=email",
            tags = { "usuario", "autenticación" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token válido"),
            @ApiResponse(responseCode = "401", description = "Credenciales no coinciden")
    })
    @PostMapping(path = "/password/recuperacion/check")
    public ResponseEntity<?> validarToken(
            @Parameter(description = "Email y token para validar en DB que coincidan", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "application/json"))
            @RequestBody String tokenResetRequest) {
        JsonObject jsonReset = new Gson().fromJson(tokenResetRequest, JsonObject.class);
        TokenReset tokenReset = new TokenReset(jsonReset.get("email").getAsString(), jsonReset.get("token").getAsString());
        if (awsService.comprobarResetToken(tokenReset)){
            return ResponseEntity.ok("Token válido");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales no coinciden");
        }
    }

    private void generarMailResetPassword(String correo, String nombre, String resetToken) throws EmailNoEnviadoException {
        Context context = new Context();
        context.setVariable("user", nombre);
        String contenido = "Estimado usuario, para generar una nueva contraseña, haga click en el enlace: " +
                env.getProperty("front.base.url") +
                "changePassword?token=" + resetToken +
                "&email=" + correo;
        System.out.println(contenido);
        context.setVariable("contenido",contenido);
        String htmlContent = templateEngine.process("reset-pass", context);
        try {
            emailService.enviarMail(correo, "Reset de Password", htmlContent, null);
        }catch (EmailNoEnviadoException e) {
            throw new EmailNoEnviadoException(e.getMessage());
        }
    }

    private boolean restauranteNoHabilitado(String correo) throws RestauranteNoEncontradoException {
        return !(restauranteService.restauranteEstado(correo) == EstadoRestaurante.ABIERTO) ||
                (restauranteService.restauranteEstado(correo) == EstadoRestaurante.CERRADO);
    }

    private boolean clienteNoHabilitado(String correo) throws ClienteNoEncontradoException {
        return !(clienteService.clienteEstado(correo) == EstadoCliente.ACTIVO);
    }
 }
