package com.example.ECommerce.Platform.Service;

import com.example.ECommerce.Platform.DTO.AuthDTO.*;
import com.example.ECommerce.Platform.DTO.ResetTokenDTO.ForgotRequestDTO;
import com.example.ECommerce.Platform.DTO.ResetTokenDTO.ResetRequestDTO;
import com.example.ECommerce.Platform.DTO.ResetTokenDTO.ResetTokenResponseDTO;
import com.example.ECommerce.Platform.Exception.*;
import com.example.ECommerce.Platform.Model.ResetToken;
import com.example.ECommerce.Platform.Model.User;
import com.example.ECommerce.Platform.Model.UserRole;
import com.example.ECommerce.Platform.Model.UserStatus;
import com.example.ECommerce.Platform.Repository.ResetTokenRepository;
import com.example.ECommerce.Platform.Repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Transactional
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ResetTokenRepository resetTokenRepository;
    @Autowired
    private EmailService emailService;

    public   RegisterResponse createAdmin(@Valid AdminRegisterRequest request) {

        if(userRepository.existsByUserEmail(request.getEmail())) {
            throw new AlreadyDoneException("Email already exists");
        }


        User admin = User.builder()
                .userName(request.getUserName())
                .userEmail(request.getEmail())
                .password(
                        passwordEncoder.encode(request.getPassword())
                )
                .phone(request.getPhone())
                .gender(request.getGender())
                .status(UserStatus.ACTIVE)
                .role(UserRole.ADMIN)
                .build();


        User savedAdmin = userRepository.save(admin);


        return mapToResponse(savedAdmin);
    }

    public  RegisterResponse registration(@Valid RegisterRequest registerRequest) {
        if(userRepository.existsByUserEmail(registerRequest.getEmail())){
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = User.builder()
                .role(UserRole.USER)
                .phone(registerRequest.getPhone())
                .userEmail(registerRequest.getEmail())
                .userName(registerRequest.getUserName())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .status(UserStatus.ACTIVE)
                .gender(registerRequest.getGender())
                .build();
        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    private  RegisterResponse mapToResponse(User savedUser) {
        RegisterResponse response = new RegisterResponse();
        response.setEmail(savedUser.getUserEmail());
        response.setUsername(savedUser.getUserName());
        response.setUserid(savedUser.getUserId());
        response.setPhone(savedUser.getPhone());
        return response;
    }

    public User authenticate(LoginRequest loginRequest) {
        User user = userRepository.findByUserEmail(loginRequest.getEmail());
        if(user==null) throw new InvalidCredentialsException("Invalid Credentials");
        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
             throw new InvalidCredentialsException("Invalid Credentials");
        return user;
    }

    public PasswordResponse changePassword(@Valid PasswordRequest passwordRequest, String email) {

        User user = userRepository.findByUserEmail(email);
        if(user == null) throw new NotFoundException("User not found");

        if(!passwordEncoder.matches(passwordRequest.getOldPass(), user.getPassword())) {
            throw new InvalidCredentialsException("Old password is incorrect");
        }
        if(passwordEncoder.matches(passwordRequest.getNewPass(), user.getPassword())){
            throw new SameException("New password cannot be same as old password");
        }

        user.setPassword(passwordEncoder.encode(passwordRequest.getNewPass()));
        userRepository.save(user);
        return new PasswordResponse("Password updated successfully");
    }

    public @Nullable ResetTokenResponseDTO forgotPassword(ForgotRequestDTO forgotRequestDTO) {
        User user = userRepository.findByUserEmail(forgotRequestDTO.getEmail());
        if(user==null) throw new NotFoundException("User not found");

        resetTokenRepository.deleteByUser(user);
        String token = generateOTP();

        ResetToken resetToken = ResetToken.builder()
                .token(token)
                .user(user)
                .expiryTime(LocalDateTime.now().plusMinutes(2))
                .build();
        resetTokenRepository.save(resetToken);
        emailService.sendOtp(user.getUserEmail(), token);
        return new ResetTokenResponseDTO("Token is successfully generated",resetToken.getExpiryTime());
    }

    public @Nullable ResetTokenResponseDTO resetPassword(@Valid ResetRequestDTO resetRequestDTO) {
        ResetToken resetToken = resetTokenRepository.findByToken(resetRequestDTO.getToken());
        if(resetToken==null) throw new InvalidTokenException("Invalid Token");

        if(resetToken.getExpiryTime().isBefore(LocalDateTime.now())){
            resetTokenRepository.delete(resetToken);
            throw new InvalidTokenException("Invalid Token");
        }
        User user = resetToken.getUser();

        if(passwordEncoder.matches( resetRequestDTO.getPassword(),user.getPassword()))
            throw new SameException("New Password cannot be same as old password");
        user.setPassword(passwordEncoder.encode(resetRequestDTO.getPassword()));
        userRepository.save(user);

        resetTokenRepository.deleteByUser(user);
        return new ResetTokenResponseDTO("Password is changed successfully",LocalDateTime.now());
    }

    public String generateOTP() {
        int otp = new Random().nextInt(900000) + 100000;
        return String.valueOf(otp);
    }
}
