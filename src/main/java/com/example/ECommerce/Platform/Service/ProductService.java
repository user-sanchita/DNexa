package com.example.ECommerce.Platform.Service;

import com.example.ECommerce.Platform.DTO.CategoryDTO.MessageDTO;
import com.example.ECommerce.Platform.DTO.ProductDTO.ProductRequestAdmin;
import com.example.ECommerce.Platform.DTO.ProductDTO.ProductResponseAdmin;
import com.example.ECommerce.Platform.DTO.ProductDTO.ProductResponseUser;
import com.example.ECommerce.Platform.Exception.*;
import com.example.ECommerce.Platform.Model.*;
import com.example.ECommerce.Platform.Repository.CategoryRepository;
import com.example.ECommerce.Platform.Repository.ProductRepository;
import com.example.ECommerce.Platform.Repository.UserRepository;
import com.example.ECommerce.Platform.Repository.VendorRepository;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
public class ProductService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private VendorRepository vendorRepository;
    @Autowired
    private UserRepository userRepository;

    public ProductResponseAdmin addProduct(ProductRequestAdmin productRequest, String email) {

        Vendor vendor = vendorRepository.findByVendorEmail(email);
        if (vendor == null)
            throw new NotFoundException("Vendor not found");

        if (!vendor.isActive() || !vendor.isVerified())
            throw new NotActivatedException("Vendor is not active");

        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        if (!category.isActive())
            throw new NotActivatedException("Category is inactive");

        boolean hasActiveSubCategory = category.getSubCategories()
                .stream()
                .anyMatch(Category::isActive);

        if (hasActiveSubCategory) {
            throw new InvalidInputException("Products can only be added to a leaf category");
        }

        if (productRepository.existsByVendorAndProductNameIgnoreCase(
                vendor,
                productRequest.getProductName())) {
            throw new AlreadyDoneException("Product already exists in your shop");
        }

        if (productRequest.getSellPrice() != null && productRequest.getPrice() != null) {
            if (productRequest.getSellPrice() < productRequest.getPrice())
                throw new SellPriceLesserThanRealPrice("Sell price cannot be lesser than real product price");
        }

        Product product = Product.builder()
                .productName(productRequest.getProductName())
                .price(productRequest.getPrice())
                .category(category)
                .vendor(vendor)
                .stock(productRequest.getStock())
                .description(productRequest.getDescription())
                .discount(productRequest.getDiscount())
                .sellPrice(productRequest.getSellPrice())
                .returnWindowInDays(productRequest.getReturnWindowInDays())
                .warrantyPeriod(productRequest.getWarrantyPeriod())
                .build();

        Product savedProduct = productRepository.save(product);

        return mapToResponseAdmin(savedProduct);
    }

    public @Nullable ProductResponseAdmin mapToResponseAdmin(Product savedProduct) {
        ProductResponseAdmin response = new ProductResponseAdmin();
        response.setProductId(savedProduct.getProductId());
        response.setDiscount(savedProduct.getDiscount());
        response.setDescription(savedProduct.getDescription());
        response.setProductName(savedProduct.getProductName());
        response.setStock(savedProduct.getStock());
        response.setPrice(savedProduct.getPrice());
        response.setSellPrice(savedProduct.getSellPrice());
        response.setCategoryId(savedProduct.getCategory().getCategoryId());
        response.setCategoryName(savedProduct.getCategory().getCategoryName());
        response.setReturnWindowInDays(savedProduct.getReturnWindowInDays());
        response.setWarrantyPeriod(savedProduct.getWarrantyPeriod());
        return response;
    }

    public @Nullable List<ProductResponseUser> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .filter(Product::isActive)
                .filter(product -> isCategoryActive(product.getCategory()))
                .map(this::mapToResponseUser)
                .toList();
    }

    public  boolean isCategoryActive(Category category) {
        while (category != null) {
            if (!category.isActive()) return false;
            category = category.getParentCategory();
        }
        return true;
    }

    public @Nullable ProductResponseUser mapToResponseUser(Product savedProduct) {
        ProductResponseUser responseUser = new ProductResponseUser();
        responseUser.setProductId(savedProduct.getProductId());
        responseUser.setProductName(savedProduct.getProductName());
        responseUser.setDescription(savedProduct.getDescription());
        responseUser.setDiscount(savedProduct.getDiscount());
        responseUser.setSellPrice(savedProduct.getSellPrice());
        responseUser.setInStock(savedProduct.getStock() > 0);
        responseUser.setDiscountedPrice(savedProduct.getSellPrice() * savedProduct.getDiscount());
        responseUser.setTotalAmount(savedProduct.getSellPrice() - responseUser.getDiscountedPrice());
        return responseUser;
    }

    public @Nullable ProductResponseUser getProductById(String productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (!product.isActive())
            throw new NotActivatedException("Product is inactive");

        if (!isCategoryActive(product.getCategory()))
            throw new NotActivatedException("Category is inactive");

        return mapToResponseUser(product);
    }

    public @Nullable List<ProductResponseUser> getProductsByCategory(String categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        if (!isCategoryActive(category))
            throw new NotActivatedException("Category is inactive");

        List<Product> products = productRepository.findByCategory_CategoryId(categoryId);

        return products.stream()
                .filter(Product::isActive)
                .map(this::mapToResponseUser)
                .toList();
    }

    public @Nullable List<ProductResponseUser> getProductByName(String name) {

        List<Product> products = productRepository.findByProductNameContainingIgnoreCase(name);

        return products.stream()
                .filter(Product::isActive)
                .filter(product -> isCategoryActive(product.getCategory()))
                .map(this::mapToResponseUser)
                .toList();
    }

    public @Nullable ProductResponseAdmin updateProducts(String productId,
                                                         ProductRequestAdmin productRequestAdmin,String email) {

        Product product = productRepository.findByProductIdAndVendorVendorEmail(productId,email)
                .orElseThrow(() -> new NotFoundException("Product not found"));


        if (productRequestAdmin.getProductName() != null
                && !product.getProductName().equalsIgnoreCase(productRequestAdmin.getProductName())) {

            if (productRepository.existsByVendorAndProductNameIgnoreCase(
                    product.getVendor(),
                    productRequestAdmin.getProductName())) {

                throw new AlreadyDoneException("Product already exists in your shop");
            }

            product.setProductName(productRequestAdmin.getProductName());
        }

        if (productRequestAdmin.getPrice() != null) {
            if (productRequestAdmin.getSellPrice() != null) {
                if(productRequestAdmin.getPrice()>productRequestAdmin.getSellPrice())
                    throw new SellPriceLesserThanRealPrice("Sell price cannot be lesser than real product price");
                product.setSellPrice(productRequestAdmin.getSellPrice());
                product.setPrice(productRequestAdmin.getPrice());
            }
            else{
                if(productRequestAdmin.getPrice()>product.getSellPrice())
                    throw new SellPriceLesserThanRealPrice("Sell price cannot be lesser than real product price");
                product.setPrice(productRequestAdmin.getPrice());
            }
        }
        if (productRequestAdmin.getSellPrice() != null) {
            if (productRequestAdmin.getPrice() == null) {
                if(productRequestAdmin.getSellPrice()<product.getPrice())
                    throw new SellPriceLesserThanRealPrice("Sell price cannot be lesser than real product price");
                product.setSellPrice(productRequestAdmin.getSellPrice());
            }

        }

        if (productRequestAdmin.getDiscount() != null)
            product.setDiscount(productRequestAdmin.getDiscount());

        if (productRequestAdmin.getDescription() != null)
            product.setDescription(productRequestAdmin.getDescription());

        if (productRequestAdmin.getStock() != null)
            product.setStock(productRequestAdmin.getStock());

        if (productRequestAdmin.getCategoryId() != null) {

            Category category = categoryRepository.findById(productRequestAdmin.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));

            if (!category.isActive())
                throw new NotActivatedException("Category is inactive");

            product.setCategory(category);
        }
        if(productRequestAdmin.getReturnWindowInDays()!=null) product.setReturnWindowInDays(productRequestAdmin.getReturnWindowInDays());
        if(productRequestAdmin.getWarrantyPeriod()!=null) product.setWarrantyPeriod(productRequestAdmin.getWarrantyPeriod());

        Product savedProduct = productRepository.save(product);

        return mapToResponseAdmin(savedProduct);
    }


    public MessageDTO deleteProduct(String productId,String email) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        User user = userRepository.findByUserEmail(email);

        if (user != null) {
            if (user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.SUPER_ADMIN)
                throw new UnAuthorizedException("User cannot delete product");
        } else {

            Vendor vendor = vendorRepository.findByVendorEmail(email);
            if (vendor == null) throw new NotFoundException("Account not found");

            if (!product.getVendor().getVendorEmail().equals(vendor.getVendorEmail()))
                throw new UnAuthorizedException("You cannot delete another vendor's product");
        }

        if(!product.isActive()) throw new AlreadyDoneException("Product is already inactive");

        product.setActive(false);

        productRepository.save(product);

        return new MessageDTO("Product deactivated");
    }

    public @Nullable MessageDTO reactivateProduct(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new NotFoundException("Product not found"));

        if(product.isActive()) throw new AlreadyDoneException("Product is already activated");
        if (!isCategoryActive(product.getCategory())) {
            throw new NotActivatedException("Category is inactive");
        }
        product.setActive(true);
        productRepository.save(product);
        return new MessageDTO("Product is reactivated successfully");
    }
}
