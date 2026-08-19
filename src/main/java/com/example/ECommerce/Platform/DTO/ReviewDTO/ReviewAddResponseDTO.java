package com.example.ECommerce.Platform.DTO.ReviewDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewAddResponseDTO {
    private String orderItemId;
    private String reviewId;
    private String productId;
    private String userName;
    private Integer rating;
    private String comment;
}
