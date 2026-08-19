package com.example.ECommerce.Platform.Security;

import com.example.ECommerce.Platform.Exception.NotFoundException;
import com.example.ECommerce.Platform.Exception.UserStatusNotActivatedException;
import com.example.ECommerce.Platform.Model.User;
import com.example.ECommerce.Platform.Model.UserStatus;
import com.example.ECommerce.Platform.Model.Vendor;
import com.example.ECommerce.Platform.Repository.UserRepository;
import com.example.ECommerce.Platform.Repository.VendorRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.NotActiveException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VendorRepository vendorRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        System.out.println("Jwt filter called: ");
        String jwt = jwtUtils.getJwtFromHeader(request);

        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        //  Invalid token → reject
        if (!jwtUtils.validateToken(jwt)) {
            throw new RuntimeException("Invalid or expired token");
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            String email = jwtUtils.getEmailFromJwt(jwt);


            User user = userRepository.findByUserEmail(email);

            if (user != null) {

                if (user.getStatus() != UserStatus.ACTIVE) {
                    throw new UserStatusNotActivatedException(
                            "User account is " + user.getStatus()
                    );
                }

            } else {

                Vendor vendor = vendorRepository.findByVendorEmail(email);

                if (vendor == null) {
                    throw new NotFoundException("User/Vendor not found");
                }

                if (!vendor.isActive() || !vendor.isVerified()) {
                    throw new NotActiveException("Vendor account is not active");
                }
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }
}
