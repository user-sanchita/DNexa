package com.example.ECommerce.Platform.Service;

import com.example.ECommerce.Platform.DTO.CartDTO.*;
import com.example.ECommerce.Platform.DTO.CategoryDTO.MessageDTO;
import com.example.ECommerce.Platform.Exception.*;
import com.example.ECommerce.Platform.Model.*;
import com.example.ECommerce.Platform.Repository.CartItemRepository;
import com.example.ECommerce.Platform.Repository.UserRepository;
import com.example.ECommerce.Platform.Repository.CartRepository;
import com.example.ECommerce.Platform.Repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductService productService;

    public CartResponseUser registeredCart(String email) {

        User user = userRepository.findByUserEmail(email);
        if(user==null) throw new NotFoundException("User Not Found");

        Cart cart = cartRepository.findByUserUserEmail(email);

        if (cart == null) {
            cart = Cart.builder()
                    .user(user)
                    .build();
            cartRepository.save(cart);
        }

        CartResponseUser response = new CartResponseUser();
        response.setCartId(cart.getCartId());
        return response;
    }

    public ItemsResponseDTO addToCart(CartItemsRequestDTO request,String email) {
        if (request.getQuantity() <= 0) {
            throw new InvalidInputException("Quantity must be greater than zero");
        }

        User user = userRepository.findByUserEmail(email);
        if(user==null) throw new NotFoundException("User Not Found");

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (!isCategoryActive(product.getCategory())) {
            throw new NotActivatedException("Category is inactive");
        }

        if (!product.isActive()) {
            throw new NotActivatedException("Product is inactive");
        }

        if (product.getStock() <= 0) {
            throw new ProductNotAvailableException("Product is out of stock");
        }

        Cart cart = cartRepository.findByUserUserEmail(email);

        if (cart == null) {
            cart = Cart.builder().user(user).cartItems(new ArrayList<>()).build();
            cartRepository.save(cart);
        }

        CartItem existingItem = cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct().getProductId().equals(product.getProductId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {

            int newQty = existingItem.getQuantity() + request.getQuantity();
            if (product.getStock() < newQty) {
                throw new ProductNotAvailableException("Only " + product.getStock() + " items available");
            }

            existingItem.setQuantity(newQty);

        } else {

            if (product.getStock() < request.getQuantity()) {
                throw new ProductNotAvailableException("Only " + product.getStock() + " items available");
            }

            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(request.getQuantity());

            cart.getCartItems().add(item);
        }

        Cart savedCart=cartRepository.save(cart);

        CartItem item = savedCart.getCartItems()
                .stream()
                .filter(i -> i.getProduct().getProductId().equals(product.getProductId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        return mapToCartItemResponse(item);
    }


    public CartGetDTO getCartByUser(String email) {

        Cart cart = cartRepository.findByUserUserEmail(email);

        if (cart == null) {
            throw new NotFoundException("Cart not found");
        }

        return mapToCartResponse(cart);
    }
    public  boolean isCategoryActive(Category category) {
        while (category != null) {
            if (!category.isActive()) return false;
            category = category.getParentCategory();
        }
        return true;
    }

    // ---------------- MAPPING ----------------
    private CartGetDTO mapToCartResponse(Cart cart) {

        List<CartItem> validItems = cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct().isActive())
                .filter(item -> isCategoryActive(item.getProduct().getCategory()))
                .toList();


        List<ItemsResponseDTO> items = validItems.stream()
                .map(this::mapToCartItemResponse)
                .toList();


        CartGetDTO response = new CartGetDTO();

        response.setCartItems(items);
        response.setTotalItems(items.size());


        int totalQty = validItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        response.setTotalQuantity(totalQty);


        double totalPrice = validItems.stream()
                .mapToDouble(item -> item.getQuantity() * item.getProduct().getSellPrice())
                .sum();

        response.setTotalPrice(totalPrice);


        double totalDiscount = validItems.stream()
                .mapToDouble(item -> {
                    double price = item.getProduct().getSellPrice();
                    float discount = item.getProduct().getDiscount();
                    int qty = item.getQuantity();

                    return price * discount * qty;
                })
                .sum();

        response.setTotalDiscount(totalDiscount);
        response.setOrderTotalPrice(totalPrice - totalDiscount);

        return response;
    }

    private ItemsResponseDTO mapToCartItemResponse(CartItem item) {

        ItemsResponseDTO dto = new ItemsResponseDTO();
        dto.setProductId(item.getProduct().getProductId());
        dto.setProductName(item.getProduct().getProductName());
        dto.setQuantity(item.getQuantity());
        dto.setSellPrice(item.getProduct().getSellPrice());

        return dto;
    }

    public CartGetDTO updateCartItemsQuantity(String email, UpdatequantityDTO update) {

        Cart cart = cartRepository.findByUserUserEmail(email);

        if (cart == null)
            throw new NotFoundException("Cart Not Found");


        CartItem cartItem = cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct().getProductId()
                        .equals(update.getProductId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("CartItem Not Found"));


        Product product = cartItem.getProduct();

        if (!product.isActive()) {
            throw new NotActivatedException("Product is inactive");
        }

        if (!isCategoryActive(product.getCategory())) {
            throw new NotActivatedException("Category is inactive");
        }

        int newQty = cartItem.getQuantity() + update.getQty();
        if (newQty <= 0) {
            cart.getCartItems().remove(cartItem);

        } else {
            if (newQty > product.getStock()) {
                throw new ProductNotAvailableException(
                        "Only " + product.getStock() + " items available"
                );
            }
            cartItem.setQuantity(newQty);
        }


        Cart savedCart = cartRepository.save(cart);
        return mapToCartResponse(savedCart);
    }

    public @Nullable MessageDTO removeCartItems(String email, String productId) {
        Cart cart = cartRepository.findByUserUserEmail(email);
        if(cart==null) throw new NotFoundException("Cart Not Found");

        CartItem cartItem = cart.getCartItems()
                .stream()
                .filter(item->item.getProduct().getProductId().equals(productId))
                .findFirst()
                .orElse(null);

        if(cartItem==null) throw new NotFoundException("CartItem Not Found");
        cart.getCartItems().remove(cartItem);
        cartRepository.save(cart);
        return new MessageDTO("CartItems removed successfully");
    }

    public @Nullable MessageDTO clearCart(String email) {
        Cart cart = cartRepository.findByUserUserEmail(email);
        if(cart==null) throw new NotFoundException("Cart Not Found");
        if(cart.getCartItems().isEmpty()) throw new AlreadyDoneException("Cart is already empty, start  shopping now");
        cart.getCartItems().clear();
        cartRepository.save(cart);

        return new MessageDTO("Cart Cleared Succssfully");
    }
}
