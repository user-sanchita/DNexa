package com.example.ECommerce.Platform.DTO.AddressDTO;

import com.example.ECommerce.Platform.Model.AddressType;
import com.example.ECommerce.Platform.Model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDTO {
    private String fullName;
    private String mobileNo;
    private String alternateMobileNumber;
    private String streetAddress;
    private String city;
    private String state;
    private String pinCode;
    private String landmark;
    private String district;
    private String country;
    private AddressType addressType;
}
