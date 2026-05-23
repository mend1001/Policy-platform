package com.segurosbolivar.polizas.repository;

import com.segurosbolivar.polizas.model.Policy;
import com.segurosbolivar.polizas.model.enums.PolicyState;
import com.segurosbolivar.polizas.model.enums.PolicyType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    List<Policy> findByTipo(PolicyType tipo);

    List<Policy> findByEstado(PolicyState estado);

    List<Policy> findByTipoAndEstado(PolicyType tipo, PolicyState estado);
}
