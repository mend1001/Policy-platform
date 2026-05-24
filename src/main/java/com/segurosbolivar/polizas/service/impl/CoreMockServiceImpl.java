package com.segurosbolivar.polizas.service.impl;

import com.segurosbolivar.polizas.dto.request.CoreEventRequest;
import com.segurosbolivar.polizas.model.IntegrationEvent;
import com.segurosbolivar.polizas.model.Policy;
import com.segurosbolivar.polizas.repository.IntegrationEventRepository;
import com.segurosbolivar.polizas.service.CoreMockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.segurosbolivar.polizas.dto.response.ApiMessages.CORE_SEND;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoreMockServiceImpl implements CoreMockService {

    private final IntegrationEventRepository integrationEventRepository;

    @Override
    public void sendEvent(CoreEventRequest request) {
        log.info(CORE_SEND + "evento={}, polizaId={}", request.getEvent(), request.getPolicyId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyCore(Policy policy, String eventType) {
        IntegrationEvent event = IntegrationEvent.builder()
                .policy(policy)
                .type(eventType)
                .state("PENDING")
                .correlationId(UUID.randomUUID())
                .payload("{\"polizaId\":\"" + policy.getId() + "\"}")
                .retries(0)
                .build();
        integrationEventRepository.save(event);

        log.info(CORE_SEND + "type={}, polId={}", eventType, policy.getId());
        event.setState("SENT");
        event.setSentAt(LocalDateTime.now());
        integrationEventRepository.save(event);
    }
}
