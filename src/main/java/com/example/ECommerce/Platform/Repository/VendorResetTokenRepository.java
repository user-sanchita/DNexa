package com.example.ECommerce.Platform.Repository;

import com.example.ECommerce.Platform.Model.Vendor;
import com.example.ECommerce.Platform.Model.VendorResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorResetTokenRepository extends JpaRepository<VendorResetToken,String> {
    void deleteByVendor(Vendor vendor);

    VendorResetToken findByToken(String token);
}
