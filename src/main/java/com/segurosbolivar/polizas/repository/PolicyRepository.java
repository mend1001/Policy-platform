package com.segurosbolivar.polizas.repository;

import com.segurosbolivar.polizas.model.Policy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PolicyRepository extends JpaRepository<Policy, UUID> {

    @EntityGraph(attributePaths = {"type", "state", "holder", "beneficiary"})
    Page<Policy> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"type", "state", "holder", "beneficiary"})
    Page<Policy> findByType_Name(String typeName, Pageable pageable);

    @EntityGraph(attributePaths = {"type", "state", "holder", "beneficiary"})
    Page<Policy> findByState_Name(String stateName, Pageable pageable);

    @EntityGraph(attributePaths = {"type", "state", "holder", "beneficiary"})
    Page<Policy> findByType_NameAndState_Name(String typeName, String stateName, Pageable pageable);

    @EntityGraph(attributePaths = {"type", "state", "holder", "beneficiary"})
    Page<Policy> findByBeneficiary_Id(UUID beneficiaryId, Pageable pageable);

    @EntityGraph(attributePaths = {"type", "state", "holder", "beneficiary"})
    Page<Policy> findByHolder_Id(UUID holderId, Pageable pageable);
}
