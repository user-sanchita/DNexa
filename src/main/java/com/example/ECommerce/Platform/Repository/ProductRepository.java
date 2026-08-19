package com.example.ECommerce.Platform.Repository;

import com.example.ECommerce.Platform.Model.Product;
import com.example.ECommerce.Platform.Model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,String> {
    List<Product> findByCategory_CategoryId(String categoryId);

    List<Product> findByProductNameContainingIgnoreCase(String name);

    List<Product> findAllByVendorVendorId(String vendorId);

    long countByVendorVendorId(String vendorId);

    boolean existsByVendorAndProductNameIgnoreCase(Vendor vendor, String productName);

    Optional<Product> findByProductIdAndVendorVendorEmail(String productId, String email);
}
