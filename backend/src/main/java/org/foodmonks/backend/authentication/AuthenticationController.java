package org.foodmonks.backend.authentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.foodmonks.backend.Admin.Admin;
import org.foodmonks.backend.Admin.AdminService;
import org.foodmonks.backend.Cliente.Cliente;
import org.foodmonks.backend.Cliente.ClienteService;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.Restaurante.RestauranteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

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
}
