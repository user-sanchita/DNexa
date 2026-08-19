package com.example.ECommerce.Platform.DTO.ReviewDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetReviewsByProductDTO {
    private String productId;
    private Integer rating;
    private String comment;
    private String userName;
}
