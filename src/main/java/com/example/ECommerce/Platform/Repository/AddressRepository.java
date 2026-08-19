package com.example.ECommerce.Platform.Repository;

import com.example.ECommerce.Platform.Model.Address;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address,String> {

    List<Address> findAllByUserUserId(String userId);

    @Modifying
    @Transactional
    @Query("""
UPDATE Address a
SET a.defaultAddress = false
WHERE a.user.userId = :userId
""")
    void resetDefaultAddressForUser(@Param("userId") String userId);

    Optional<Address> findByAddressIdAndUserUserEmail(String addressId, String email);

    List<Address> findByUserUserEmail(String email);

    boolean existsByUser_UserEmail(String email);
}
