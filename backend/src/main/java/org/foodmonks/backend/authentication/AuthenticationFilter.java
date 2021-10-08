package org.foodmonks.backend.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationFilter extends OncePerRequestFilter {

    private TokenHelper tokenHelper;
    private CustomService customService;
    private boolean testing = true;

    public AuthenticationFilter(CustomService customService, TokenHelper tokenHelper) {
        this.tokenHelper = tokenHelper;
        this.customService = customService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        String authToken=tokenHelper.getToken(request);
        if(!testing) {
            System.out.println(authToken);
            this.testing = true;
        } else {
            this.testing = false;
        }

        if(null!=authToken) {

            String userName=tokenHelper.getUsernameFromToken(authToken);

            if(null!=userName) {

                //no puedo instanciar Usuario por ser abstracta
                //reemplazar por el casteo del usuario al tipo correspondiente(try-catchs) usando CustomService
                UserDetails userDetails=userDetailsService.loadUserByUsername(userName);

                if(tokenHelper.validateToken(authToken, userDetails)) {
                    //las authorities es el rol y de esta forma queda enevidencia dado que sabemos que tipo de usuario es
                    //todas las authorities pasan a ser Strings
                    UsernamePasswordAuthenticationToken authentication=new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);



                }

            }

        }

        filterChain.doFilter(request, response);



    }
}
