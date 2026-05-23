package com.segurosbolivar.polizas.repository;

import com.segurosbolivar.polizas.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PolicyRepository extends JpaRepository<Policy, UUID> {

    List<Policy> findByType_Name(String typeName);

    List<Policy> findByState_Name(String stateName);

    List<Policy> findByType_NameAndState_Name(String typeName, String stateName);
}
