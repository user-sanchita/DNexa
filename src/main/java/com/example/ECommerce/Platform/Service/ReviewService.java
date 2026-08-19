package com.example.ECommerce.Platform.Service;

import com.example.ECommerce.Platform.DTO.ReviewDTO.GetReviewsByProductDTO;
import com.example.ECommerce.Platform.DTO.ReviewDTO.ReviewAddRequestDTO;
import com.example.ECommerce.Platform.DTO.ReviewDTO.ReviewAddResponseDTO;
import com.example.ECommerce.Platform.DTO.ReviewDTO.ReviewUpdateRequestDTO;
import com.example.ECommerce.Platform.Exception.AlreadyDoneException;
import com.example.ECommerce.Platform.Exception.FollowedByAnotherStatusException;
import com.example.ECommerce.Platform.Exception.NotFoundException;
import com.example.ECommerce.Platform.Model.*;
import com.example.ECommerce.Platform.Repository.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
public class ReviewService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ProductRepository productRepository;

    public  ReviewAddResponseDTO addReview(ReviewAddRequestDTO reviewAddRequestDTO,String email) {
        User user = userRepository.findByUserEmail(email);
        if(user==null) throw new NotFoundException("User Not Found");

        OrderItem orderItem = orderItemRepository
                .findByOrderItemIdAndOrdersUserUserId(reviewAddRequestDTO.getOrderItemId(),user.getUserId());
        if(orderItem==null) throw new NotFoundException("OrderItem Not Found");

        if(orderItem.getDeliveredAt()==null)
            throw new FollowedByAnotherStatusException("Only delivered products can be reviewed");

        if(reviewRepository.existsByOrderItem(orderItem)) throw new AlreadyDoneException("Review already exists");


        Product product = orderItem.getProduct();
        if(product==null) throw new NotFoundException("Product Not Found");

        Review review = Review.builder()
                .user(user)
                .orderItem(orderItem)
                .rating(reviewAddRequestDTO.getRating())
                .comment(reviewAddRequestDTO.getComment())
                .product(product)
                .build();

        Review savedReview = reviewRepository.save(review);
        return mapToReviewAddResponse(savedReview);
    }

    private ReviewAddResponseDTO mapToReviewAddResponse(Review savedReview) {
        ReviewAddResponseDTO responseDTO = new ReviewAddResponseDTO();

        responseDTO.setOrderItemId(savedReview.getOrderItem().getOrderItemId());
        responseDTO.setComment(savedReview.getComment());
        responseDTO.setRating(savedReview.getRating());
        responseDTO.setProductId(savedReview.getProduct().getProductId());
        responseDTO.setReviewId(savedReview.getReviewId());
        responseDTO.setUserName(savedReview.getUser().getUserName());
        return responseDTO;
    }

    public  ReviewAddResponseDTO updateReview(@Valid ReviewUpdateRequestDTO reviewUpdateRequestDTO,String email) {
        User user = userRepository.findByUserEmail(email);
        if(user==null) throw new NotFoundException("User Not Found");

        OrderItem orderItem = orderItemRepository
                .findByOrderItemIdAndOrdersUserUserId(reviewUpdateRequestDTO.getOrderItemId(),user.getUserId());
        if(orderItem==null) throw new NotFoundException("OrderItem Not Found");

        if(!reviewRepository.existsByOrderItem(orderItem)) throw new FollowedByAnotherStatusException("Review Does n't exists, So you can't update it.please give a review");

        Review review = reviewRepository.findByOrderItem(orderItem);
        if(reviewUpdateRequestDTO.getRating()!=null) review.setRating(reviewUpdateRequestDTO.getRating());
        if(reviewUpdateRequestDTO.getComment()!=null) review.setComment(reviewUpdateRequestDTO.getComment());

        Review saved = reviewRepository.save(review);
        return mapToReviewAddResponse(saved);
    }

    public @Nullable List<GetReviewsByProductDTO> getReviewsByProduct(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new NotFoundException("Product Not Found"));

        List<Review> reviews = product.getReviews();
        if(reviews==null) throw new NotFoundException("No reviews for this Product");

        return reviews.stream()
                .map(this::mapToGetReviewsByProduct)
                .toList();
    }

    private GetReviewsByProductDTO mapToGetReviewsByProduct(Review review) {
        GetReviewsByProductDTO response = new GetReviewsByProductDTO();

        response.setProductId(review.getProduct().getProductId());
        response.setUserName(review.getUser().getUserName());
        response.setRating(review.getRating());
        response.setComment(review.getComment());

        return response;
    }
}
