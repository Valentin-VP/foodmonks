package org.foodmonks.backend.authentication;

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
import java.security.Principal;
import java.security.spec.InvalidKeySpecException;

@CrossOrigin(origins = "http://localhost:3000")
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
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest) throws InvalidKeySpecException, NoSuchAlgorithmException {

        final Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getEmail(), authenticationRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails usuario = customService.loadUserByUsername(authenticationRequest.getEmail());

        String jwtToken=tokenHelper.generateToken(usuario.getUsername(), usuario.getAuthorities());
        System.out.println("el token es: " + jwtToken);

        //falta generar el refreshToken y agregarselo a la response
        AuthenticationResponse response=new AuthenticationResponse();
        response.setToken(jwtToken);

        return ResponseEntity.ok(response);
    }

    //endpoint para renovar los tokens, debe recibir el correo del usuario(esta en el refreshToken)

    @GetMapping("/auth/userinfo")
    public ResponseEntity<?> getUserInfo(Authentication user) {
        InfoUsuario userInfo = new InfoUsuario();

        if (adminService.buscarAdmin(user.getName()) != null) {
            Admin admin = adminService.buscarAdmin(user.getName());
            userInfo.setFirstName(admin.getNombre());
            userInfo.setLastName(admin.getApellido());
            userInfo.setRoles(admin.getAuthorities().toArray());
            return new ResponseEntity<>(userInfo, HttpStatus.OK);

        } else if (restauranteService.buscarRestaurante(user.getName()) != null) {
            Restaurante restaurante = restauranteService.buscarRestaurante(user.getName());
            userInfo.setFirstName(restaurante.getNombre());
            userInfo.setLastName(restaurante.getApellido());
            userInfo.setRoles(restaurante.getAuthorities().toArray());
            return new ResponseEntity<>(userInfo, HttpStatus.OK);

        } else if (clienteService.buscarCliente(user.getName()) != null) {
            Cliente cliente = clienteService.buscarCliente(user.getName());
            System.out.println(cliente.getUsername());
            userInfo.setFirstName(cliente.getNombre());
            userInfo.setLastName(cliente.getApellido());
            userInfo.setRoles(cliente.getAuthorities().toArray());
            return new ResponseEntity<>(userInfo, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("no se encontro ningun tipo de usuario", HttpStatus.BAD_REQUEST);
        }
    }
}
