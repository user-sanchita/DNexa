package com.example.ECommerce.Platform.DTO.VendorDTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVendorDTO {

    private String vendorName;
    @Column(length=10)
    private String contactNumber;
    private String shopName;
    private String shopDescription;

    private String pickupAddress;
    private String city;
    private String state;
    private String pinCode;


    private String accountHolderName;
    private String accountNumber;
    private String bankName;
    private String ifscCode;

    @Pattern(
            regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",
            message = "Invalid GST number"
    )
    @Column(unique = true)
    private String gstNumber;
    private String panNumber;
}
