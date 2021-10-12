package org.foodmonks.backend.authentication;

import org.foodmonks.backend.Admin.Admin;
import org.foodmonks.backend.Cliente.Cliente;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.springframework.beans.factory.annotation.Autowired;
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


    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest) throws InvalidKeySpecException, NoSuchAlgorithmException {

        System.out.println(authenticationRequest.getCorreo());
        final Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getCorreo(), authenticationRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        //Usuario usuario=(Usuario)authentication.getPrincipal();// ***
        UserDetails usuario = customService.loadUserByUsername(authenticationRequest.getCorreo());//alternativa: try-catch con ***

        String jwtToken=tokenHelper.generateToken(usuario.getUsername(), usuario.getAuthorities());
        //falta generar el refreshToken y agregarselo a la response
        AuthenticationResponse response=new AuthenticationResponse();
        response.setToken(jwtToken);

        return ResponseEntity.ok(response);
    }

    //endpoint para renovar los tokens, debe recibir el correo del usuario(esta en el refreshToken)

    @GetMapping("/auth/userinfo")
    public ResponseEntity<?> getUserInfo(Principal user){//proximamente: ver una forma de ahorrar codigo
        InfoUsuario userInfo = new InfoUsuario();

        try {
            Admin admin = (Admin) user;
            userInfo.setFirstName(admin.getNombre());
            userInfo.setLastName(admin.getApellido());
            userInfo.setRoles(admin.getAuthorities().toArray());
        } catch(ClassCastException a) {
            try {
                Restaurante restaurante = (Restaurante) user;
                userInfo.setFirstName(restaurante.getNombre());
                userInfo.setLastName(restaurante.getApellido());
                userInfo.setRoles(restaurante.getAuthorities().toArray());
            } catch(ClassCastException r) {
                Cliente cliente = (Cliente) user;
                userInfo.setFirstName(cliente.getNombre());
                userInfo.setLastName(cliente.getApellido());
                userInfo.setRoles(cliente.getAuthorities().toArray());
            }
        }

        return ResponseEntity.ok(userInfo);

    }

//    public InfoUsuario getInfoUsuario(Usuario user) {
//        InfoUsuario userInfo=new InfoUsuario();
//
//        userInfo.setFirstName(user.getNombre());
//        userInfo.setLastName(user.getApellido());
//        userInfo.setRoles(user.getAuthorities().toArray());
//
//        return userInfo;
//    }
}
