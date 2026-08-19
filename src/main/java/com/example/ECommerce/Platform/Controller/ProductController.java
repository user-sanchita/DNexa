package com.example.ECommerce.Platform.Controller;

import com.example.ECommerce.Platform.DTO.CategoryDTO.MessageDTO;
import com.example.ECommerce.Platform.DTO.ProductDTO.ProductRequestAdmin;
import com.example.ECommerce.Platform.DTO.ProductDTO.ProductResponseAdmin;
import com.example.ECommerce.Platform.DTO.ProductDTO.ProductResponseUser;
import com.example.ECommerce.Platform.Service.ProductService;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("/add/products")
    public ResponseEntity<ProductResponseAdmin> addProduct(@RequestBody ProductRequestAdmin productRequest, Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(productService.addProduct(productRequest,email));
    }
    @GetMapping("/getall/products")
    public ResponseEntity<List<ProductResponseUser>> getAllProducts(){
        return ResponseEntity.ok(productService.getAllProducts());
    }
    @GetMapping("/get/productbyId/{productId}")
    public ResponseEntity<ProductResponseUser> getProductById(@PathVariable String productId){
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @GetMapping("/get/productbyCategory/{categoryId}")
    public ResponseEntity<List<ProductResponseUser>>  getProductsByCategory(@PathVariable String categoryId){
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }

    @GetMapping("/get/productByName/{name}")
    public ResponseEntity<List<ProductResponseUser>> getProductByName(@PathVariable String name){
        return ResponseEntity.ok(productService.getProductByName(name));
    }
    @PatchMapping("/update/product/{productId}")
    public ResponseEntity<ProductResponseAdmin> updateProducts(@PathVariable String productId,@RequestBody ProductRequestAdmin productRequestAdmin,Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(productService.updateProducts(productId,productRequestAdmin,email));
    }

    @DeleteMapping("/delete/product/{productId}")
    public ResponseEntity<MessageDTO> deleteProduct(@PathVariable String productId,Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(productService.deleteProduct(productId,email));
    }
    @PutMapping("/reactivate/product/{productId}")
    public ResponseEntity<MessageDTO> reactivateProduct(@PathVariable String productId){
        return ResponseEntity.ok(productService.reactivateProduct(productId));
    }
}
