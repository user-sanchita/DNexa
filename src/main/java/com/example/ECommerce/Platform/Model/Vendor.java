package com.example.ECommerce.Platform.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vendors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String vendorId;

    private String vendorName;

    @NotBlank(message = "Email can't be null")
    @Email(message = "Invalid Email")
    @Column(nullable = false, unique = true)
    private String vendorEmail;


    @NotBlank(message = "Password can't be null")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
            message = "Password must be at least 8 characters long and " +
                    "include uppercase, lowercase, number and special character"
    )
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Contact number required")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Invalid contact number"
    )
    @Column(nullable = false,length=10)
    private String contactNumber;

    @NotBlank(message = "Shop name required")
    @Column(nullable = false)
    private String shopName;


    private String shopDescription;

    @Pattern(
            regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",
            message = "Invalid GST number"
    )
    @Column(unique = true)
    private String gstNumber;

    private String panNumber;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private UserRole role= UserRole.VENDOR;


    private String bankAccountNo;

    private String bankName;
    @Pattern(
            regexp = "^[A-Z]{4}0[A-Z0-9]{6}$",
            message = "Invalid IFSC code"
    )
    private String ifscCode;

    private String accountHolderName;

    private String pickupAddress;
    private String city;
    private String state;
    private String pinCode;

    @Builder.Default
    private boolean isActive = false;
    @Builder.Default
    private boolean isVerified = false;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VendorStatus vendorStatus = VendorStatus.PENDING;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Product> products=new ArrayList<>();

}
