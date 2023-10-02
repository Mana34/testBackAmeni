package com.itgate.ProShift.security.jwt;

import com.itgate.ProShift.entity.JwtToken;
import com.itgate.ProShift.repository.JwtTokenRepository;
import com.itgate.ProShift.security.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {
  @Autowired
  private JwtUtils jwtTokenUtil;
  @Autowired
  private UserDetailsServiceImpl jwtUserDetailsService;
  @Autowired
  private JwtTokenRepository jwtTokenRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
    final String header = request.getHeader("Authorization");

    if (header == null || !header.startsWith("Bearer ")) {
      chain.doFilter(request, response);
      return;
    }

    final String token = header.substring(7);
    UserDetails userDetails = null;

    JwtToken jwtToken = jwtTokenRepository.findByToken(token);

    if (jwtToken != null && !jwtTokenUtil.isTokenExpired(jwtToken.getToken())) {
      String username = jwtTokenUtil.getUsernameFromToken(token);
      userDetails =  jwtUserDetailsService.loadUserByUsername(username);
    }

    if (userDetails != null && SecurityContextHolder.getContext().getAuthentication() == null) {

      userDetails = jwtUserDetailsService.loadUserByUsername(jwtTokenUtil.getUsernameFromToken(token));
      UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(
                      userDetails,
                      null,
                      userDetails.getAuthorities());
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    chain.doFilter(request, response);
  }


}
