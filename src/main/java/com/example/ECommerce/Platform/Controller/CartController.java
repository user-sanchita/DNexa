package com.example.ECommerce.Platform.Controller;

import com.example.ECommerce.Platform.DTO.CartDTO.*;
import com.example.ECommerce.Platform.DTO.CategoryDTO.MessageDTO;
import com.example.ECommerce.Platform.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/register")
    public ResponseEntity<CartResponseUser> registerCart(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(cartService.registeredCart(email));
    }


    @PostMapping("/addTocart")
    public ResponseEntity<ItemsResponseDTO> addToCart(@RequestBody CartItemsRequestDTO request,Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(cartService.addToCart(request,email));
    }

    @GetMapping("/get/cartbyUser")
    public ResponseEntity<CartGetDTO> getCartByUser(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(cartService.getCartByUser(email));
    }
    @PutMapping("/update/cartItems/quantity")
    public ResponseEntity<CartGetDTO> updateCartItemsQuantity(Authentication authentication,@RequestBody UpdatequantityDTO update){
        String email = authentication.getName();
        return ResponseEntity.ok(cartService.updateCartItemsQuantity(email,update));
    }
    @DeleteMapping("/remove/cartItems")
    public ResponseEntity<MessageDTO>removeCartItems(Authentication authentication,@RequestParam String productId){
        String email = authentication.getName();
        return ResponseEntity.ok(cartService.removeCartItems(email,productId));
    }
    @DeleteMapping("/clear/cart")
    public ResponseEntity<MessageDTO>clearCart(Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(cartService.clearCart(email));
    }

}
