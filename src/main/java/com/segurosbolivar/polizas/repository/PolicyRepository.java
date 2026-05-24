package com.segurosbolivar.polizas.repository;

import com.segurosbolivar.polizas.model.Policy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PolicyRepository extends JpaRepository<Policy, UUID> {

    Page<Policy> findByType_Name(String typeName, Pageable pageable);

    Page<Policy> findByState_Name(String stateName, Pageable pageable);

    Page<Policy> findByType_NameAndState_Name(String typeName, String stateName, Pageable pageable);

    Page<Policy> findByBeneficiary_Id(UUID beneficiaryId, Pageable pageable);

    Page<Policy> findByHolder_Id(UUID holderId, Pageable pageable);
}
