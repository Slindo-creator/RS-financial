package com.afrofuturists.rsfinancial.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.afrofuturists.rsfinancial.domain.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {

    List<Client> findByAdviserId(UUID adviserId);

    boolean existsByEmail(String email);
}
