package org.foodmonks.backend.authentication;

import org.foodmonks.backend.Usuario.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private UserDetailsService CustomService;


    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest) throws InvalidKeySpecException, NoSuchAlgorithmException {

        System.out.println(authenticationRequest.getCorreo());
        final Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getCorreo(), authenticationRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Usuario usuario=(Usuario)authentication.getPrincipal();
        //Principal es el correo?? puedo manejar un UserDetails a Cliente, Restaurante y Admin???

        String jwtToken=tokenHelper.generateToken(usuario.getCorreo(), usuario.getAuthorities());
        //falta generar el refreshToken y agregarselo a la response
        AuthenticationResponse response=new AuthenticationResponse();
        response.setToken(jwtToken);

        return ResponseEntity.ok(response);
    }

    //endpoint para renovar los tokens

    @GetMapping("/auth/userinfo")
    public ResponseEntity<?> getUserInfo(Principal user){
        Usuario userObj=(Usuario) CustomService.loadUserByUsername(user.getName());
        //puedo manejar un UserDetails a Cliente, Restaurante y Admin???

        InfoUsuario userInfo=new InfoUsuario();
        userInfo.setFirstName(userObj.getNombre());
        userInfo.setLastName(userObj.getApellido());
        userInfo.setRoles(userObj.getAuthorities().toArray());


        return ResponseEntity.ok(userInfo);



    }
}
