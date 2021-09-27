package com.example.expensetrackerapi.filters;

import com.example.expensetrackerapi.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        String authHeader = httpServletRequest.getHeader("Authorization");
        if (authHeader != null) {
            String[] authHeaderArr = authHeader.split("Bearer");

            if (authHeaderArr.length > 1 && authHeaderArr[1] != null) {
                String token = authHeaderArr[1];
                try {
                    Claims claims = Jwts.parser().setSigningKey(Constants.API_SECRET_KEY)
                            .parseClaimsJws(token).getBody();

                    httpServletRequest.setAttribute("userId", Integer.parseInt(claims.get("userId").toString()));
                } catch (Exception e) {
                    httpServletResponse.sendError(HttpStatus.FORBIDDEN.value(), "not logged in");
                    return;
                }
            } else {
                httpServletResponse.sendError(HttpStatus.FORBIDDEN.value(), "Authorization failed");
                return;
            }
        } else {
            httpServletResponse.sendError(HttpStatus.FORBIDDEN.value(), "Authorization values insufficient");
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
