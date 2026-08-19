package com.example.ECommerce.Platform.Controller;

import com.example.ECommerce.Platform.DTO.ReviewDTO.GetReviewsByProductDTO;
import com.example.ECommerce.Platform.DTO.ReviewDTO.ReviewAddRequestDTO;
import com.example.ECommerce.Platform.DTO.ReviewDTO.ReviewAddResponseDTO;
import com.example.ECommerce.Platform.DTO.ReviewDTO.ReviewUpdateRequestDTO;
import com.example.ECommerce.Platform.Service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping("/add/review")
    public ResponseEntity<ReviewAddResponseDTO> addReview(@Valid @RequestBody ReviewAddRequestDTO reviewAddRequestDTO, Authentication authentication){
        String email = authentication.getName();;
        return ResponseEntity.ok(reviewService.addReview(reviewAddRequestDTO,email));
    }
    @PatchMapping("/update/review")
    public ResponseEntity<ReviewAddResponseDTO> updateReview(@Valid @RequestBody ReviewUpdateRequestDTO reviewUpdateRequestDTO,Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(reviewService.updateReview(reviewUpdateRequestDTO,email));
    }

    @GetMapping("/get/reviewsByProduct/{productId}")
    public ResponseEntity<List<GetReviewsByProductDTO>> getReviewsByProduct(@PathVariable String productId){
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
    }
}
