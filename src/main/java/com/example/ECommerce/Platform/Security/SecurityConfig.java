package com.example.ECommerce.Platform.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private JwtFilter jwtFilter;
    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http){
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .authorizeHttpRequests(authorizeRequests
                        ->authorizeRequests
                        .requestMatchers("/auth/register", "/auth/login", "/auth/forgot/password", "/auth/reset/password", "/user/reactivate/**").permitAll()
                        .requestMatchers("/auth/change/password", "/auth/logout").authenticated()
                        .requestMatchers("/user/get/userprofile", "/user/update/userprofile", "/user/delete/userac","/dashboard/user").hasRole("USER")
                        .requestMatchers("/user/admin/allusers", "/user/admin/userid/**", "/user/admin/updatedstatus","/dashboard/admin").hasAnyRole("ADMIN","SUPER_ADMIN")
                        .requestMatchers("/vendor/register/vendor", "/vendor/forgot/password", "/vendor/reset/password","/vendor/login").permitAll()
                        .requestMatchers("/vendor/get/**", "/vendor/update/**", "/vendor/change/vendorPassword","/dashboard/vendor").hasRole("VENDOR")
                        .requestMatchers("/vendor/{vendorId}").hasAnyRole("ADMIN","SUPER_ADMIN")
                        .requestMatchers("/category/get/categories", "/category/getbyId/category/**").permitAll()
                        .requestMatchers("/category/add/categories","/category/update/categories",
                                "/category/update/category/name/**", "/category/delete/category/**", "/category/reactive/category/**").hasAnyRole("ADMIN","SUPER_ADMIN")
                        .requestMatchers("/product/getall/products", "/product/get/productbyId/**",
                                "/product/get/productbyCategory/**", "/product/get/productByName/**", "/payment/success", "/payment/success/now","/review/get/reviewsByProduct/**").permitAll()
                        .requestMatchers("/product/add/products", "/product/update/product/**", "/product/delete/product/**","/return/vendorRaise/dispute").hasRole("VENDOR")
                        .requestMatchers("/product/reactivate/product/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/cart/register", "/cart/addTocart", "/cart/get/cartbyUser", "/cart/update/cartItems/quantity", "/cart/remove/cartItems", "/cart/clear/cart").hasRole("USER")
                        .requestMatchers("/orders/place/orders", "/orders/order/now", "/orders/order/ById/**", "/orders/my/orders", "/orders/cancel/order","/return/request").hasRole("USER")
                        .requestMatchers("/orders/admin/status/**", "/orders/admin/getOrder/ById/**", "/orders/adminGet/all/orders",
                                "/return/admin/review","/return/get/allReturns","/payment/get/byOrderId", "/payment/get/allPayments",
                                "/payment/get/paymentId","/payment/refund/complete","/address/get/allAddresses/byAdmin").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/address/add/address","/address/get/allAddresses", "/address/get/addressById",
                                "/address/update/address", "/address/default/address","/address/delete/address").hasRole("USER")
                        .requestMatchers("/add/toWishlist","/get/wishlist","/remove/fromWishlist","/review/add/review","/review/update/review").hasRole("USER")
                        .requestMatchers("/swagger-ui/**","/swagger-ui.html","/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                );
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder();
    }

}
