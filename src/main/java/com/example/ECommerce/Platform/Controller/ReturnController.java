package com.example.ECommerce.Platform.Controller;

import com.example.ECommerce.Platform.DTO.CategoryDTO.MessageDTO;
import com.example.ECommerce.Platform.DTO.ReturnDTO.AdminGetResponseDTO;
import com.example.ECommerce.Platform.DTO.ReturnDTO.AdminReviewRequestDTO;
import com.example.ECommerce.Platform.DTO.ReturnDTO.ReturnRequestDTO;
import com.example.ECommerce.Platform.DTO.ReturnDTO.VendorDisputeRequest;
import com.example.ECommerce.Platform.Service.ReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/return")
public class ReturnController {

    @Autowired
    private ReturnService returnService;

    @PostMapping("/request")
    public ResponseEntity<MessageDTO> returnRequest(@RequestBody ReturnRequestDTO requestDTO, Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(returnService.returnRequest(requestDTO,email));
    }

    @PatchMapping("/admin/review")
    public ResponseEntity<MessageDTO> adminReview(@RequestBody AdminReviewRequestDTO adminReviewRequestDTO, Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(returnService.adminReview(adminReviewRequestDTO,email));
    }

    @PatchMapping("/vendorRaise/dispute")
    public ResponseEntity<MessageDTO> vendorRaiseDispute(@RequestBody VendorDisputeRequest vendorDisputeRequest, Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(returnService.vendorRaiseDispute(vendorDisputeRequest,email));
    }


    @GetMapping("/get/allReturns")
    public ResponseEntity<List<AdminGetResponseDTO>> getAllReturns(Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(returnService.getAllReturns(email));
    }

}
