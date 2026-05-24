package com.segurosbolivar.polizas.repository;

import com.segurosbolivar.polizas.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByDocNumber(String docNumber);
}
