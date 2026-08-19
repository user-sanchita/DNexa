package com.example.ECommerce.Platform.DTO.VendorDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponseVendor {
    private String vendorId;
    private String vendorName;
    private String vendorEmail;
    private String contactNumber;
    private String shopName;
    private String shopDescription;
    private String pickupAddress;
    private String city;
    private String state;
    private String pinCode;
    private boolean isActive;
    private boolean isVerified;
}
