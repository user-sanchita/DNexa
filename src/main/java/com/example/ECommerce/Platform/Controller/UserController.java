package com.example.ECommerce.Platform.Controller;

import com.example.ECommerce.Platform.DTO.ResetTokenDTO.ForgotRequestDTO;
import com.example.ECommerce.Platform.DTO.ResetTokenDTO.ResetTokenResponseDTO;
import com.example.ECommerce.Platform.DTO.UserDTO.AdminResponseDTO;
import com.example.ECommerce.Platform.DTO.UserDTO.AdminStatusDTO;
import com.example.ECommerce.Platform.DTO.UserDTO.UserRequestDTO;
import com.example.ECommerce.Platform.DTO.UserDTO.UserResponseDTO;
import com.example.ECommerce.Platform.Model.UserStatus;
import com.example.ECommerce.Platform.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/get/userprofile")
    public ResponseEntity<UserResponseDTO>getUserProfile(Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(userService.getUserProfile(email));
    }

    @PatchMapping("/update/userprofile")
    public ResponseEntity<UserResponseDTO> upadtedUserProfile(@Valid @RequestBody UserRequestDTO userRequestDTO,Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(userService.updatedUserProfile(userRequestDTO,email));
    }

    @GetMapping("/admin/allusers")
    public ResponseEntity<List<AdminResponseDTO>> getAllUsers(@RequestParam(required = false,defaultValue = "10") int size,
                                                              @RequestParam(required = false,defaultValue = "0") int pageNo,
                                                              @RequestParam(required = false,defaultValue = "userName") String sortBy,
                                                              @RequestParam(required = false,defaultValue = "asc") String sortDir,
                                                              @RequestParam(required = false) String userName){
        Sort sort = null;
        if(sortDir.equalsIgnoreCase("ASC")) sort = Sort.by(sortBy).ascending();
        else if (sortDir.equalsIgnoreCase("DESC")) sort = Sort.by(sortBy).descending();
        else throw new IllegalArgumentException("Invalid Sort Direction");
        return ResponseEntity.ok(userService.getAllUsers(PageRequest.of(pageNo,size,sort),userName));
    }

    @GetMapping("/admin/userid/{userId}")
    public ResponseEntity<AdminResponseDTO> getUserByUserId(@PathVariable String userId){
        return ResponseEntity.ok(userService.getUserByUserId(userId));
    }

    @PatchMapping("/admin/updatedstatus")
    public ResponseEntity<AdminStatusDTO> updatedStatus(@RequestHeader String userId, @RequestHeader UserStatus status,Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(userService.updatedStatus(userId,status,email));
    }

    @DeleteMapping("/delete/userac")
    public ResponseEntity<Void> deleteUserAccount(Authentication authentication) {
        String email = authentication.getName();
        userService.deleteUserAccount(email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reactivate/send-otp")
    public ResponseEntity<ResetTokenResponseDTO> reactivateAccount(@RequestBody ForgotRequestDTO forgotRequestDTO){
        return ResponseEntity.ok(userService.reactivateAccount(forgotRequestDTO));
    }
    @PostMapping("/reactivate/verify")
    public ResponseEntity<AdminStatusDTO> reactivateVerify(@RequestParam String otp ) {

        return ResponseEntity.ok(userService.reactivateVerify( otp));
    }
}
