package com.example.ECommerce.Platform.DTO.ReturnDTO;

import com.example.ECommerce.Platform.Model.ReturnReason;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnRequestDTO {
    @NotBlank(message = "Order item ID cannot be empty")
    private String orderItemId;
    @NotBlank(message = "Reason cannot be empty")
    private ReturnReason reason;
    @NotBlank(message = "Quantity cannot be empty")
    private Integer returnQty;


    private String refundType;
    private String bankAccountNo;
    private String bankName;
    private String ifscCode;
    private String accountHolderName;


}
