package com.example.ECommerce.Platform.Controller;

import com.example.ECommerce.Platform.DTO.AuthDTO.LoginRequest;
import com.example.ECommerce.Platform.DTO.AuthDTO.LoginResponse;
import com.example.ECommerce.Platform.DTO.AuthDTO.PasswordRequest;
import com.example.ECommerce.Platform.DTO.AuthDTO.PasswordResponse;
import com.example.ECommerce.Platform.DTO.CategoryDTO.MessageDTO;
import com.example.ECommerce.Platform.DTO.ResetTokenDTO.ForgotRequestDTO;
import com.example.ECommerce.Platform.DTO.ResetTokenDTO.ResetRequestDTO;
import com.example.ECommerce.Platform.DTO.ResetTokenDTO.ResetTokenResponseDTO;
import com.example.ECommerce.Platform.DTO.VendorDTO.*;
import com.example.ECommerce.Platform.Model.OrderStatus;
import com.example.ECommerce.Platform.Model.Vendor;
import com.example.ECommerce.Platform.Security.JwtUtils;
import com.example.ECommerce.Platform.Service.VendorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendor")
public class VendorController {
    @Autowired
    private VendorService vendorService;
    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/get/productsByVendor")
    public ResponseEntity<List<VendorResponseDTO>> getProductsByVendor(@RequestParam String vendorId, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(vendorService.getProductsByVendor(vendorId, email));
    }

    @GetMapping("/get/vendorOrders")
    public ResponseEntity<List<VendorOrdersResponseDTO>> getVendorOrders(@RequestParam String vendorId, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(vendorService.getVendorOrders(vendorId, email));
    }

    @GetMapping("/get/vendorReturns")
    public ResponseEntity<List<VendorReturnResponseDTO>> getVendorReturns(@RequestParam String vendorId, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(vendorService.getVendorReturns(vendorId, email));
    }

    @PostMapping("register/vendor")
    public ResponseEntity<RegisterResponseVendor> registerVendor(@Valid @RequestBody RegisterVendor registerVendor){
        return ResponseEntity.ok(vendorService.registerVendor(registerVendor));
    }

    @PatchMapping("/update/vendorProfile")
    public ResponseEntity<RegisterResponseVendor> updateVendorProfile(@Valid @RequestBody UpdateVendorDTO updateVendorDTO,Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(vendorService.updateVendorProfile(updateVendorDTO,email));
    }

    @PatchMapping("/change/vendorPassword")
    public ResponseEntity<PasswordResponse> changeVendorPassword(@Valid @RequestBody PasswordRequest passwordRequest,Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(vendorService.changeVendorPassword(passwordRequest,email));
    }

    @PostMapping("/forgot/password")
    public ResponseEntity<ResetTokenResponseDTO> forgotVendorPassword(@RequestBody ForgotRequestDTO forgotRequestDTO){
        return ResponseEntity.ok(vendorService.forgotVendorPassword(forgotRequestDTO));
    }

    @PostMapping("/reset/password")
    public ResponseEntity<ResetTokenResponseDTO> resetVendorPassword( @Valid @RequestBody ResetRequestDTO resetRequestDTO){
        return ResponseEntity.ok(vendorService.resetVendorPassword(resetRequestDTO));
    }

    @PatchMapping("/update/orderStatus")
    public ResponseEntity<MessageDTO> vendorUpdateOrderStatus(@RequestParam String orderItemId, @RequestParam OrderStatus status, Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(vendorService.vendorUpdateOrderStatus(orderItemId,status,email));
    }

    @PutMapping("/{vendorId}")
    public ResponseEntity<RegisterResponseVendor> approveOrRejectVendor(@PathVariable String vendorId, @RequestParam boolean approve) {

        return ResponseEntity.ok(vendorService.approveOrRejectVendor(vendorId, approve));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> vendorLogin(@RequestBody LoginRequest loginRequest){
        Vendor vendor = vendorService.authenticate(loginRequest);

        String token = jwtUtils.generateToken(
                vendor.getVendorEmail(),
                vendor.getRole().name()
        );

        return ResponseEntity.ok(
                new LoginResponse(token)
        );
    }


}

