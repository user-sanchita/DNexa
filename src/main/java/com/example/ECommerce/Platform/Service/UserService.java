package com.example.ECommerce.Platform.Service;

import com.example.ECommerce.Platform.DTO.ResetTokenDTO.ForgotRequestDTO;
import com.example.ECommerce.Platform.DTO.ResetTokenDTO.ResetTokenResponseDTO;
import com.example.ECommerce.Platform.DTO.UserDTO.AdminResponseDTO;
import com.example.ECommerce.Platform.DTO.UserDTO.AdminStatusDTO;
import com.example.ECommerce.Platform.DTO.UserDTO.UserRequestDTO;
import com.example.ECommerce.Platform.DTO.UserDTO.UserResponseDTO;
import com.example.ECommerce.Platform.Exception.AlreadyDoneException;
import com.example.ECommerce.Platform.Exception.InvalidInputException;
import com.example.ECommerce.Platform.Exception.InvalidTokenException;
import com.example.ECommerce.Platform.Exception.NotFoundException;
import com.example.ECommerce.Platform.Model.ResetToken;
import com.example.ECommerce.Platform.Model.User;
import com.example.ECommerce.Platform.Model.UserRole;
import com.example.ECommerce.Platform.Model.UserStatus;
import com.example.ECommerce.Platform.Repository.ResetTokenRepository;
import com.example.ECommerce.Platform.Repository.UserRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ResetTokenRepository resetTokenRepository;
    @Autowired
    private EmailService emailService;


//User profile by user
    public @Nullable UserResponseDTO getUserProfile(String email) {
        User user = userRepository.findByUserEmail(email);
        if(user==null) throw new NotFoundException("User Not Found");
        return mapToResponse(user);
    }

    private @Nullable UserResponseDTO mapToResponse(User user) {
        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setUserId(user.getUserId());
        responseDTO.setPhone(user.getPhone());
        responseDTO.setEmail(user.getUserEmail());
        responseDTO.setUserName(user.getUserName());
        responseDTO.setGender(user.getGender());
        return responseDTO;
    }

//admin check all user profile
    public @Nullable List<AdminResponseDTO> getAllUsers(Pageable pageable,String userName) {
        List<User> users = null;

        if(userName==null) users = userRepository.findAll(pageable).getContent();
        else users = userRepository.findByUserName(pageable,userName).getContent();

        return users.stream()
                .map(this::mapToResponseAdmin)
                .collect(Collectors.toList());

    }

//admin check a single user profile by Id
    public @Nullable AdminResponseDTO getUserByUserId(String userId) {
        User user = userRepository.findById(userId).orElseThrow(()->new NotFoundException("User not found "+userId));
        return mapToResponseAdmin(user);
    }

    private @Nullable AdminResponseDTO mapToResponseAdmin(User user) {
        AdminResponseDTO responseDTO = new AdminResponseDTO();
        responseDTO.setEmail(user.getUserEmail());
        responseDTO.setUserId(user.getUserId());
        responseDTO.setRole(user.getRole());
        responseDTO.setGender(user.getGender());
        responseDTO.setPhone(user.getPhone());
        responseDTO.setUserName(user.getUserName());
        responseDTO.setStatus(user.getStatus());
        return responseDTO;
    }
//admin update status
    public @Nullable AdminStatusDTO updatedStatus(String userId, UserStatus status,String email) {
        User user = userRepository.findById(userId).orElseThrow(()->new NotFoundException("User not found"+userId));
        if(user.getStatus()==status) throw new AlreadyDoneException("User status already "+status);

        User admin = userRepository.findByUserEmail(email);
        if(admin==null) throw new NotFoundException("Admin Not Found");

        if (user.getRole() == UserRole.SUPER_ADMIN)
            throw new InvalidInputException(
                    "Super Admin status cannot be changed."
            );

        if (admin.getRole() != UserRole.SUPER_ADMIN
                && user.getRole() == UserRole.ADMIN)

            throw new InvalidInputException(
                    "Only Super Admin can update Admin status."
            );

        if (admin.getUserId().equals(user.getUserId())) {
            throw new InvalidInputException(
                    "You cannot change your own status."
            );
        }


        user.setStatus(status);
        userRepository.save(user);


        return mapToResponseStatus(user);
    }

    private @Nullable AdminStatusDTO mapToResponseStatus(User user) {
        AdminStatusDTO response = new AdminStatusDTO();
        response.setUserId(user.getUserId());
        response.setStatus(user.getStatus());
        return response;
    }
//user update their profile
    public @Nullable UserResponseDTO updatedUserProfile(UserRequestDTO userRequestDTO,String email) {
        User user = userRepository.findByUserIdAndUserEmail(userRequestDTO.getUserId(),email).orElseThrow(()
                ->new NotFoundException("User not found "+userRequestDTO.getUserId()));

        if(userRequestDTO.getUserName()!=null) user.setUserName(userRequestDTO.getUserName());
        if(userRequestDTO.getGender()!=null) user.setGender(userRequestDTO.getGender());
        if(userRequestDTO.getPhone()!=null) user.setPhone(userRequestDTO.getPhone());

        return mapToResponse(userRepository.save(user));
    }

    public ResponseEntity<Void> deleteUserAccount(String email) {
        User user = userRepository.findByUserEmail(email);
        if(user==null) return ResponseEntity.status(404).build();

        if (user.getRole() == UserRole.ADMIN ||
                user.getRole() == UserRole.SUPER_ADMIN) {

            throw new InvalidInputException(
                    "Admin or Super Admin cannot deactivate their own account."
            );
        }

        if(user.getStatus()!=UserStatus.DEACTIVATED && user.getStatus()!=UserStatus.BLOCKED){
            user.setStatus(UserStatus.DEACTIVATED);
            userRepository.save(user);
        }
        else throw new AlreadyDoneException("User status already Deactivated/Blocked");
        return ResponseEntity.ok().build();
    }

    public ResetTokenResponseDTO reactivateAccount(ForgotRequestDTO forgotRequestDTO) {
        User user = userRepository.findByUserEmail(forgotRequestDTO.getEmail());

        if (user == null)
            throw new NotFoundException("User not found");

        if (user.getStatus() == UserStatus.ACTIVE)
            throw new AlreadyDoneException("Account is already active");

        if (user.getStatus() == UserStatus.BLOCKED)
            throw new InvalidInputException(
                    "Your account has been blocked by admin."
            );

        resetTokenRepository.deleteByUser(user);
        String token = generateOTP();

        ResetToken resetToken = ResetToken.builder()
                .token(token)
                .user(user)
                .expiryTime(LocalDateTime.now().plusMinutes(5))
                .build();
        resetTokenRepository.save(resetToken);
        emailService.sendOtp(user.getUserEmail(), token);
        return new ResetTokenResponseDTO("Token is successfully generated",resetToken.getExpiryTime());
    }
    public String generateOTP() {
        int otp = new Random().nextInt(900000) + 100000;
        return String.valueOf(otp);
    }

    public  AdminStatusDTO reactivateVerify(String otp) {

        ResetToken resetToken = resetTokenRepository.findByToken(otp);
        if(resetToken==null) throw new InvalidTokenException("Invalid Token");

        if(resetToken.getExpiryTime().isBefore(LocalDateTime.now())){
            resetTokenRepository.delete(resetToken);
            throw new InvalidTokenException("Invalid Token");
        }
        User user = resetToken.getUser();
        if (user == null)
            throw new NotFoundException("User not found");

        if (user.getStatus() == UserStatus.ACTIVE)
            throw new AlreadyDoneException("Account is already active");

        if (user.getStatus() == UserStatus.BLOCKED)
            throw new InvalidInputException(
                    "Your account has been blocked by admin."
            );
        user.setStatus(UserStatus.ACTIVE);
        resetTokenRepository.delete(resetToken);

        return mapToResponseStatus(user);
    }
}
