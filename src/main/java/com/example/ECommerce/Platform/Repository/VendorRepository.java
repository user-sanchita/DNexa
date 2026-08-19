package com.example.ECommerce.Platform.Repository;

import com.example.ECommerce.Platform.Model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor,String> {

    Optional<Object> findByVendorIdAndVendorEmail(String vendorId, String email);

    boolean existsByVendorEmail(String vendorEmail);


    Optional<Vendor> findByVendorId(String vendorId);

    Vendor findByVendorEmail(String email);
}
