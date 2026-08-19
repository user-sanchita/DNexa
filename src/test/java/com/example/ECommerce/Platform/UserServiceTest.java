package com.example.ECommerce.Platform;

import com.example.ECommerce.Platform.DTO.UserDTO.*;
import com.example.ECommerce.Platform.DTO.ResetTokenDTO.*;
import com.example.ECommerce.Platform.Model.*;
import com.example.ECommerce.Platform.Exception.*;

import com.example.ECommerce.Platform.Repository.ResetTokenRepository;
import com.example.ECommerce.Platform.Repository.UserRepository;
import com.example.ECommerce.Platform.Service.EmailService;
import com.example.ECommerce.Platform.Service.UserService;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ResetTokenRepository resetTokenRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    //Get UserProfile
    @Test
    void testGetUserProfile_WhenUserExists() {

        // Arrange
        User user = new User();
        user.setUserId("user-1");
        user.setUserName("User");
        user.setGender("Male");
        user.setUserEmail("user@gmail.com");
        user.setPassword("User@123");

        when(userRepository.findByUserEmail("user@gmail.com"))
                .thenReturn(user);

        // Act
        UserResponseDTO result = userService.getUserProfile("user@gmail.com");

        // Assert
        assertNotNull(result);
        assertEquals("user-1", result.getUserId());
        assertEquals("User", result.getUserName());
        assertEquals("Male", result.getGender());
        assertEquals("user@gmail.com", result.getEmail());

        verify(userRepository).findByUserEmail("user@gmail.com");
    }

    @Test
    void testGetUserProfile_WhenUserNotExists(){
        when(userRepository.findByUserEmail("wrong@gmail.com")).thenReturn(null);

//        UserResponseDTO responseDTO = userService.getUserProfile("wrong@gmail.com");
//        assertNull(responseDTO); amake nicher line ta likhte hobe karon oporer line ei
//        exception throw hobe tai ei line ta execute e hobe na

        assertThrows(NotFoundException.class,()-> userService.getUserProfile("wrong@gmail.com"));

        verify(userRepository).findByUserEmail("wrong@gmail.com");
    }
//Get All Users
    @Test
    void testGetAllUsers_withoutName(){

        //arrange
        Pageable pageable = PageRequest.of(0, 10);

        User user1 = new User();
        user1.setUserId("user-1");
        user1.setUserName("Ram");
        user1.setGender("Male");
        user1.setUserEmail("ram@gmail.com");
        user1.setPassword("Ram@123");

        User user2 = new User();
        user2.setUserId("user-2");
        user2.setUserName("Madhu");
        user2.setGender("Female");
        user2.setUserEmail("madhu@gmail.com");
        user2.setPassword("Madhu@123");

        List<User> users = List.of(user1,user2);
        Page<User> page = new PageImpl<>(users);

        when(userRepository.findAll(pageable)).thenReturn(page);

        //act

        List<AdminResponseDTO> result = userService.getAllUsers(pageable,null);

        //assert

        assertEquals(2,result.size());
        assertEquals("Ram",result.get(0).getUserName());
        assertEquals("Madhu",result.get(1).getUserName());

        //verify

        verify(userRepository).findAll(pageable);
    }

    @Test
    void testGetAllUsers_withName(){

        //arrange
        Pageable pageable = PageRequest.of(0, 10);

        User user1 = new User();
        user1.setUserId("user-1");
        user1.setUserName("Ram");
        user1.setGender("Male");
        user1.setUserEmail("ram@gmail.com");
        user1.setPassword("Ram@123");


        List<User> users = List.of(user1);
        Page<User> page = new PageImpl<>(users);

        when(userRepository.findByUserName(pageable,"Ram")).thenReturn(page);

        //act

        List<AdminResponseDTO> result2 = userService.getAllUsers(pageable,"Ram");

        //assert

        assertEquals(1,result2.size());
        assertEquals("Ram",result2.get(0).getUserName());

        //verify

        verify(userRepository).findByUserName(pageable,"Ram");
    }

    @Test
    void testGetAllUser_ifUsersNotExists(){
        //arrange
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        //act

        List<AdminResponseDTO> result3 = userService.getAllUsers(pageable,null);

        //assert
        assertTrue(result3.isEmpty());

        //verify

        verify(userRepository).findAll(pageable);
    }

    //Updated User Profile
    @Test
    void testUpdateUserProfile_whenUserExists(){
        // Arrange
        User user = new User();
        user.setUserId("user-1");
        user.setUserName("User");
        user.setGender("Male");
        user.setPhone("8912635464");
        user.setUserEmail("user@gmail.com");
        user.setPassword("User@123");

        UserRequestDTO requestDTO = new UserRequestDTO();
        requestDTO.setUserId("user-1");
        requestDTO.setGender("Female");
        requestDTO.setUserName("Madhu");
        requestDTO.setPhone("9831371264");

        when(userRepository.findByUserIdAndUserEmail("user-1","user@gmail.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        //act

        UserResponseDTO responseDTO = userService.updatedUserProfile(requestDTO,"user@gmail.com");

        //assert

        assertEquals("Madhu",responseDTO.getUserName());
        assertEquals("Female",requestDTO.getGender());

        //verify

        verify(userRepository).findByUserIdAndUserEmail("user-1","user@gmail.com");
        verify(userRepository).save(user);
    }



}


