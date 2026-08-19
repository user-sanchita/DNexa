package com.example.ECommerce.Platform.DTO.ReturnDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorDisputeRequest {

   private String returnId;
    private String vendorReason;
    private String proofImageUrl;
}
