package com.afrofuturists.rsfinancial.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.afrofuturists.rsfinancial.domain.StaffUser;

@Repository
public interface StaffUserRepository extends JpaRepository<StaffUser, UUID> {

    Optional<StaffUser> findByUsername(String username);

    boolean existsByUsername(String username);
}
