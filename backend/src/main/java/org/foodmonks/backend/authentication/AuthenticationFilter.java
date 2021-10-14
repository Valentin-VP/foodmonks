package org.foodmonks.backend.authentication;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationFilter extends OncePerRequestFilter {

    private TokenHelper tokenHelper;
    private UserDetailsService customService;
    private boolean testing = true;

    public AuthenticationFilter(UserDetailsService customService, TokenHelper tokenHelper) {
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

                UserDetails userDetails=customService.loadUserByUsername(userName);

                if(tokenHelper.validateToken(authToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication=new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);



                }

            }

        }//si authtoken es null, tengo que validar el refreshToken

        filterChain.doFilter(request, response);



    }
}
