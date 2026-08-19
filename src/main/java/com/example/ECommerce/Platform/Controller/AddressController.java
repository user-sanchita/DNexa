package com.example.ECommerce.Platform.Controller;

import com.example.ECommerce.Platform.DTO.AddressDTO.AddressRequestDTO;
import com.example.ECommerce.Platform.DTO.AddressDTO.AddressResponseDTO;
import com.example.ECommerce.Platform.DTO.CategoryDTO.MessageDTO;
import com.example.ECommerce.Platform.Model.Address;
import com.example.ECommerce.Platform.Service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {
    @Autowired
    private AddressService addressService;

    @PostMapping("/add/address")
    public ResponseEntity<AddressResponseDTO> addAddress(Authentication authentication, @Valid @RequestBody AddressRequestDTO requestDTO) {
        String email = authentication.getName();
        return ResponseEntity.ok(addressService.addAddress(email, requestDTO));
    }

    @GetMapping("/get/allAddresses/byAdmin")
    public ResponseEntity<List<AddressResponseDTO>> getAllAddressesByUserId(@RequestParam String userId) {
        return ResponseEntity.ok(addressService.getAllAddressesByUserId(userId));
    }

    @GetMapping("/get/allAddresses")
    public ResponseEntity<List<AddressResponseDTO>> getAllAddresses(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(addressService.getAllAddresses(email));
    }

    @GetMapping("/get/addressById")
    public ResponseEntity<AddressResponseDTO> getAddressById(@RequestParam String addressId, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(addressService.getAddressById(addressId, email));
    }

    @PatchMapping("/update/address")
    public ResponseEntity<AddressResponseDTO> updateAddress(@RequestBody AddressRequestDTO requestDTO, Authentication authentication,@RequestParam String addressId) {
        String email = authentication.getName();
        return ResponseEntity.ok(addressService.updateAddress(requestDTO, email,addressId));
    }

    @DeleteMapping("/delete/address")
    public ResponseEntity<MessageDTO> deleteAddress(@RequestParam String addressId, Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(addressService.deleteAddress(addressId,email));
    }

    @PatchMapping("/default/address")
    public ResponseEntity<MessageDTO> setDefaultAddress(@RequestParam String addressId, Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(addressService.setDefaultAddress(addressId,email));
    }

}
