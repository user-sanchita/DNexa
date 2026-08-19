package com.example.ECommerce.Platform.Controller;

import com.example.ECommerce.Platform.DTO.AuthDTO.*;
import com.example.ECommerce.Platform.DTO.ResetTokenDTO.ForgotRequestDTO;
import com.example.ECommerce.Platform.DTO.ResetTokenDTO.ResetRequestDTO;
import com.example.ECommerce.Platform.DTO.ResetTokenDTO.ResetTokenResponseDTO;
import com.example.ECommerce.Platform.Model.User;
import com.example.ECommerce.Platform.Security.JwtUtils;
import com.example.ECommerce.Platform.Service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registration(@Valid @RequestBody RegisterRequest registerRequest){
        return ResponseEntity.ok(authService.registration(registerRequest));
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<RegisterResponse> createAdmin(@Valid @RequestBody AdminRegisterRequest request) {
        return ResponseEntity.ok(authService.createAdmin(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){
        User user = authService.authenticate(loginRequest);
        String token = jwtUtils.generateToken(user.getUserEmail(),user.getRole().name());
        return ResponseEntity.ok(new LoginResponse(token));
    }
    @PatchMapping("/change/password")
    public ResponseEntity<PasswordResponse> changePassword(@Valid @RequestBody PasswordRequest passwordRequest, Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(authService.changePassword(passwordRequest,email));
    }

    @PostMapping("/forgot/password")
    public ResponseEntity<ResetTokenResponseDTO> forgotPassword(@RequestBody ForgotRequestDTO forgotRequestDTO){
        return ResponseEntity.ok(authService.forgotPassword(forgotRequestDTO));
    }


    @PostMapping("/reset/password")
    public ResponseEntity<ResetTokenResponseDTO> forgotPassword( @Valid @RequestBody ResetRequestDTO resetRequestDTO){
        return ResponseEntity.ok(authService.resetPassword(resetRequestDTO));
    }

    @PostMapping("/logout") // everything about it handled by frontend
    public ResponseEntity<String> logoutUser() {
        return ResponseEntity.ok("Logout successful");
    }
}
