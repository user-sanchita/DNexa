package com.example.ECommerce.Platform.DTO.VendorDTO;

import com.example.ECommerce.Platform.Model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterVendor {
    private String vendorName;
    private String vendorEmail;
    private String password;
    private String contactNumber;
    private String shopName;
    private String shopDescription;
    private String gstNumber;
    private String panNumber;
    private String bankAccountNo;
    private String bankName;
    private String ifscCode;
    private String accountHolderName;
    private String pickupAddress;
    private String city;
    private String state;
    private String pinCode;

}
