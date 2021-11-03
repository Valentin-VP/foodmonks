package org.foodmonks.backend.authentication;


import lombok.SneakyThrows;
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

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        String token=tokenHelper.getRefreshToken(request);
        boolean renovar = false;

        if(token != null) {
            String userName = tokenHelper.getUsernameFromToken(token);
            String authToken=tokenHelper.getToken(request);

            if(authToken != null) {
                userName=tokenHelper.getUsernameFromToken(authToken);
                token = authToken;
            } else {
                renovar = true;
            }

            UserDetails userDetails = customService.loadUserByUsername(userName);
            if(renovar) {
                String jwtToken = tokenHelper.generateToken(userDetails.getUsername(), userDetails.getAuthorities());
                String jwtRefreshToken = tokenHelper.generateRefreshToken(userDetails.getUsername(), userDetails.getAuthorities());
                response.setHeader("Authorization", jwtToken);
                response.setHeader("RefreshAuthentication", jwtRefreshToken);
            }
            if(tokenHelper.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authentication=new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
        }

        filterChain.doFilter(request, response);



    }
}
