package com.insha.moneymanager.repository;

import com.insha.moneymanager.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {

    //select * from profile where email = ?1
    Optional<ProfileEntity> findByEmail(String email);

    //select * from profile where activation_token = ?1
    Optional<ProfileEntity> findByActivationToken(String activationToken);
}
