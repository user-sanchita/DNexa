package com.example.ECommerce.Platform.DTO.ReturnDTO;

import com.example.ECommerce.Platform.Model.ReturnStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminReviewRequestDTO {
   private String returnId;
   private ReturnStatus status;
   private String adminComment;
}
