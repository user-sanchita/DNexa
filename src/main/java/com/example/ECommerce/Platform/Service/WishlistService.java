package com.example.ECommerce.Platform.Service;

import com.example.ECommerce.Platform.DTO.CategoryDTO.MessageDTO;
import com.example.ECommerce.Platform.DTO.WishlistDTO.WishlistResponseDTO;
import com.example.ECommerce.Platform.Exception.AlreadyDoneException;
import com.example.ECommerce.Platform.Exception.NotFoundException;
import com.example.ECommerce.Platform.Model.Product;
import com.example.ECommerce.Platform.Model.User;
import com.example.ECommerce.Platform.Model.WishlistItem;
import com.example.ECommerce.Platform.Repository.ProductRepository;
import com.example.ECommerce.Platform.Repository.UserRepository;
import com.example.ECommerce.Platform.Repository.WishlistRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Transactional
@Service
public class WishlistService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private WishlistRepository wishlistRepository;

    public  MessageDTO addToWishlist(String email, String productId) {
        User user = userRepository.findByUserEmail(email);
        if(user==null) throw new NotFoundException("User Not Found");
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new NotFoundException("Product Not Found"));
        if(wishlistRepository
                .existsByUserUserEmailAndProductProductId(
                        email,productId)){
            throw new AlreadyDoneException(
                    "Product is already in your wishlist.");
        }

        WishlistItem wishlistItem = WishlistItem.builder()
                .user(user)
                .product(product)
                .build();
        wishlistRepository.save(wishlistItem);
        return new MessageDTO("Product saved in your Wishlist");
    }

    public  List<WishlistResponseDTO> getWishlist(String email) {
        User user = userRepository.findByUserEmail(email);
        if(user==null) throw new NotFoundException("User Not Found");


        List<WishlistItem> items = wishlistRepository.findByUserUserId(user.getUserId());
        return items.stream()
                .map(this::mapToWishlistResponse)
                .toList();
    }



    private WishlistResponseDTO mapToWishlistResponse(WishlistItem item) {
        WishlistResponseDTO responseDTO = new WishlistResponseDTO();

        Product product = item.getProduct();
        responseDTO.setProductId(product.getProductId());
        responseDTO.setProductName(product.getProductName());
        Double finalPrice = product.getSellPrice()*(1- product.getDiscount());
        responseDTO.setFinalPrice(finalPrice);

        return responseDTO;
    }

    public  MessageDTO removeFromWishlist(String email, String productId) {

        if(!wishlistRepository.existsByUserUserEmailAndProductProductId(email,productId))
            throw new NotFoundException( "Product not found in Wishlist");

        wishlistRepository.deleteByUserUserEmailAndProductProductId(email,productId);

        return new MessageDTO("Product is removed from your Wishlist");
    }
}
