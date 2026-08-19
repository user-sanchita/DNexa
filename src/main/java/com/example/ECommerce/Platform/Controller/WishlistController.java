package com.example.ECommerce.Platform.Controller;

import com.example.ECommerce.Platform.DTO.CategoryDTO.MessageDTO;
import com.example.ECommerce.Platform.DTO.WishlistDTO.WishlistResponseDTO;
import com.example.ECommerce.Platform.Model.WishlistItem;
import com.example.ECommerce.Platform.Service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {
    @Autowired
    private WishlistService wishlistService;

    @PostMapping("/add/toWishlist")
    public ResponseEntity<MessageDTO> addToWishlist(Authentication authentication, @RequestParam String productId){
        return ResponseEntity.ok(wishlistService.addToWishlist(authentication.getName(), productId));
    }

    @GetMapping("/get/wishlist")
    public ResponseEntity<List<WishlistResponseDTO>> getWishlist(Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(wishlistService.getWishlist(email));
    }

    @DeleteMapping("/remove/fromWishlist")
    public ResponseEntity<MessageDTO> removeFromWishlist(Authentication authentication, @RequestParam String productId){
        return ResponseEntity.ok(wishlistService.removeFromWishlist(authentication.getName(), productId));
    }
}
