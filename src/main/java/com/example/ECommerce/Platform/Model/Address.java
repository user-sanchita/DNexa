package com.example.ECommerce.Platform.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String addressId;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false,length=10)
    private String mobileNo;

    @Column(length=10)
    private String alternateMobileNumber;

    @Column(nullable = false)
    private String streetAddress;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false, length = 6)
    private String pinCode;

    private String landmark;

    private String district;

    private String country;

    @Enumerated(EnumType.STRING)
    private AddressType addressType;

    private boolean defaultAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
