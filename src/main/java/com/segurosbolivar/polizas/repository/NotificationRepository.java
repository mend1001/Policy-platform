package com.segurosbolivar.polizas.repository;

import com.segurosbolivar.polizas.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByPolicy_Id(UUID policyId);
    List<Notification> findByState(String state);
}
