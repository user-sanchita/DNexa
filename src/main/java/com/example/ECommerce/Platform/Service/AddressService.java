package com.example.ECommerce.Platform.Service;

import com.example.ECommerce.Platform.DTO.AddressDTO.AddressRequestDTO;
import com.example.ECommerce.Platform.DTO.AddressDTO.AddressResponseDTO;
import com.example.ECommerce.Platform.DTO.CategoryDTO.MessageDTO;
import com.example.ECommerce.Platform.Exception.AlreadyDoneException;
import com.example.ECommerce.Platform.Exception.NotFoundException;
import com.example.ECommerce.Platform.Exception.UnAuthorizedException;
import com.example.ECommerce.Platform.Model.Address;
import com.example.ECommerce.Platform.Model.User;
import com.example.ECommerce.Platform.Repository.AddressRepository;
import com.example.ECommerce.Platform.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
public class AddressService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AddressRepository addressRepository;

    @Transactional
    public AddressResponseDTO addAddress(String email, AddressRequestDTO dto) {
        User user = userRepository.findByUserEmail(email);
        if(user==null) throw new NotFoundException("User Not Found");

        boolean isFirstAddress = !addressRepository.existsByUser_UserEmail(email);


        Address address = new Address();

        address.setFullName(dto.getFullName());
        address.setMobileNo(dto.getMobileNo());
        address.setAlternateMobileNumber(dto.getAlternateMobileNumber());
        address.setStreetAddress(dto.getStreetAddress());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPinCode(dto.getPinCode());
        address.setLandmark(dto.getLandmark());
        address.setDistrict(dto.getDistrict());
        address.setCountry(dto.getCountry() != null ? dto.getCountry() : "India");
        address.setAddressType(dto.getAddressType());
        address.setDefaultAddress(isFirstAddress);
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);
        return mapToAddressResponse(savedAddress);
    }

    private AddressResponseDTO mapToAddressResponse(Address savedAddress) {
        AddressResponseDTO dto = new AddressResponseDTO();

        dto.setFullName(savedAddress.getFullName());
        dto.setAddressId(savedAddress.getAddressId());
        dto.setMobileNo(savedAddress.getMobileNo());
        dto.setAlternateMobileNumber(savedAddress.getAlternateMobileNumber());
        dto.setStreetAddress(savedAddress.getStreetAddress());
        dto.setCity(savedAddress.getCity());
        dto.setState(savedAddress.getState());
        dto.setPinCode(savedAddress.getPinCode());
        dto.setLandmark(savedAddress.getLandmark());
        dto.setDistrict(savedAddress.getDistrict());
        dto.setCountry(savedAddress.getCountry());
        dto.setAddressType(savedAddress.getAddressType());
        dto.setDefaultAddress(savedAddress.isDefaultAddress());

        return dto;
    }

    public @Nullable List<AddressResponseDTO> getAllAddressesByUserId(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new NotFoundException("User Not Found"));

        List<Address> addresses = addressRepository.findAllByUserUserId(userId);

        return  addresses.stream()
                .map(this::mapToAddressResponse)
                .toList();
    }

    public  AddressResponseDTO getAddressById(String addressId,String email) {

        Address address = addressRepository.findById(addressId)
                .orElseThrow(()->new NotFoundException("Address Not Found"));

        if (!address.getUser().getUserEmail().equals(email)) {
            throw new UnAuthorizedException("Access Denied");
        }

        return mapToAddressResponse(address);

    }

    public  AddressResponseDTO updateAddress(AddressRequestDTO requestDTO,String email,String addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(()->new NotFoundException("Address Not Found"));

        if(!address.getUser().getUserEmail().equals(email))
            throw new UnAuthorizedException("Access Denied");

        if (requestDTO.getFullName() != null && !requestDTO.getFullName().isBlank())
            address.setFullName(requestDTO.getFullName());
        if (requestDTO.getMobileNo() != null && !requestDTO.getMobileNo().isBlank())
            address.setMobileNo(requestDTO.getMobileNo());
        if (requestDTO.getAlternateMobileNumber() != null && !requestDTO.getAlternateMobileNumber().isBlank())
            address.setAlternateMobileNumber(requestDTO.getAlternateMobileNumber());
        if (requestDTO.getStreetAddress() != null && !requestDTO.getStreetAddress().isBlank())
            address.setStreetAddress(requestDTO.getStreetAddress());
        if (requestDTO.getCity() != null && !requestDTO.getCity().isBlank())
            address.setCity(requestDTO.getCity());
        if (requestDTO.getState() != null && !requestDTO.getState().isBlank())
            address.setState(requestDTO.getState());
        if (requestDTO.getPinCode() != null && !requestDTO.getPinCode().isBlank())
            address.setPinCode(requestDTO.getPinCode());
        if (requestDTO.getLandmark() != null && !requestDTO.getLandmark().isBlank())
            address.setLandmark(requestDTO.getLandmark());
        if (requestDTO.getDistrict() != null && !requestDTO.getDistrict().isBlank())
            address.setDistrict(requestDTO.getDistrict());
        if (requestDTO.getCountry() != null && !requestDTO.getCountry().isBlank())
            address.setCountry(requestDTO.getCountry());
        if (requestDTO.getAddressType() != null) address.setAddressType(requestDTO.getAddressType());

        Address savedAddress = addressRepository.save(address);

        return mapToAddressResponse(savedAddress);
    }

    public MessageDTO deleteAddress(String addressId, String email) {

        Address address = addressRepository
                .findByAddressIdAndUserUserEmail(addressId, email)
                .orElseThrow(() -> new NotFoundException("Address Not Found"));

        boolean isDefault = address.isDefaultAddress();

        addressRepository.delete(address);

        if (isDefault) {

            List<Address> remainingAddresses =
                    addressRepository.findByUserUserEmail(email);

            if (!remainingAddresses.isEmpty()) {
                Address newDefault = remainingAddresses.get(0);
                newDefault.setDefaultAddress(true);
                addressRepository.save(newDefault);
            }
        }

        return new MessageDTO("Address deleted successfully");
    }

    public MessageDTO setDefaultAddress(String addressId, String email) {
        Address address = addressRepository.findByAddressIdAndUserUserEmail(addressId,email)
                .orElseThrow(()->new NotFoundException("Address Not Found"));

        if(address.isDefaultAddress()) throw new AlreadyDoneException("It's already default");

        addressRepository.resetDefaultAddressForUser(address.getUser().getUserId());
        address.setDefaultAddress(true);
        addressRepository.save(address);

        return new MessageDTO("Default address set successfully");
    }

    public @Nullable List<AddressResponseDTO> getAllAddresses(String email) {
        User user = userRepository.findByUserEmail(email);
        if(user==null) throw new NotFoundException("User Not Found");

        List<Address> addresses = addressRepository.findAllByUserUserId(user.getUserId());

        return  addresses.stream()
                .map(this::mapToAddressResponse)
                .toList();
    }
}

