package com.example.ECommerce.Platform.Model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class DeliveryAddress {

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
}
