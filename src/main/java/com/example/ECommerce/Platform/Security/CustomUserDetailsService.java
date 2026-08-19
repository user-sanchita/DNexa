package com.example.ECommerce.Platform.Security;

import com.example.ECommerce.Platform.Model.User;
import com.example.ECommerce.Platform.Model.Vendor;
import com.example.ECommerce.Platform.Repository.UserRepository;
import com.example.ECommerce.Platform.Repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VendorRepository vendorRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByUserEmail(email);
        if (user != null) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUserEmail())
                    .password(user.getPassword())
                    .roles(user.getRole().name())
                    .build();
        }

        Vendor vendor = vendorRepository.findByVendorEmail(email);

        if (vendor != null) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(vendor.getVendorEmail())
                    .password(vendor.getPassword())
                    .roles(vendor.getRole().name())
                    .build();
        }

        throw new UsernameNotFoundException("User/Vendor not found");
    }
}
