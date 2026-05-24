package com.segurosbolivar.polizas.service;

import com.segurosbolivar.polizas.model.IntegrationEvent;
import com.segurosbolivar.polizas.model.Policy;
import com.segurosbolivar.polizas.model.User;
import com.segurosbolivar.polizas.model.catalog.PolicyState;
import com.segurosbolivar.polizas.model.catalog.PolicyType;
import com.segurosbolivar.polizas.repository.IntegrationEventRepository;
import com.segurosbolivar.polizas.service.impl.CoreMockServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoreMockServiceImplTest {

    @Mock
    private IntegrationEventRepository integrationEventRepository;

    private CoreMockServiceImpl coreMockService;

    @BeforeEach
    void setUp() {
        coreMockService = new CoreMockServiceImpl(integrationEventRepository);
    }

    @Test
    void deberiaGuardarIntegrationEventYActualizarEstadoASent() {
        Policy policy = polizaBase();
        when(integrationEventRepository.save(any(IntegrationEvent.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        coreMockService.notifyCore(policy, "POLICY_RENEWED");

        ArgumentCaptor<IntegrationEvent> captor = ArgumentCaptor.forClass(IntegrationEvent.class);
        verify(integrationEventRepository, times(2)).save(captor.capture());

        IntegrationEvent finalEvent = captor.getValue();
        assertThat(finalEvent.getState()).isEqualTo("SENT");
        assertThat(finalEvent.getSentAt()).isNotNull();
    }

    @Test
    void deberiaPersistirEventoConTipoYPolizaCorrectos() {
        Policy policy = polizaBase();
        ArgumentCaptor<IntegrationEvent> captor = ArgumentCaptor.forClass(IntegrationEvent.class);
        when(integrationEventRepository.save(any(IntegrationEvent.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        coreMockService.notifyCore(policy, "POLICY_CANCELLED");

        verify(integrationEventRepository, times(2)).save(captor.capture());
        IntegrationEvent event = captor.getAllValues().get(0);
        assertThat(event.getType()).isEqualTo("POLICY_CANCELLED");
        assertThat(event.getPolicy()).isEqualTo(policy);
    }

    @Test
    void deberiaTenerCorrelationIdUnico() {
        Policy policy = polizaBase();
        when(integrationEventRepository.save(any(IntegrationEvent.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        coreMockService.notifyCore(policy, "POLICY_RENEWED");

        ArgumentCaptor<IntegrationEvent> captor = ArgumentCaptor.forClass(IntegrationEvent.class);
        verify(integrationEventRepository, times(2)).save(captor.capture());
        assertThat(captor.getAllValues().get(0).getCorrelationId()).isNotNull();
    }

    private Policy polizaBase() {
        User user = User.builder().id(UUID.randomUUID()).name("Test").email("test@test.com")
                .docType("CC").docNumber("12345678").build();
        return Policy.builder()
                .id(UUID.randomUUID())
                .type(PolicyType.builder().id(UUID.randomUUID()).name("INDIVIDUAL").build())
                .state(PolicyState.builder().id(UUID.randomUUID()).name("ACTIVA").build())
                .holder(user).beneficiary(user)
                .canon(new BigDecimal("1000000"))
                .premium(new BigDecimal("12000000"))
                .months(12)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .risks(new ArrayList<>())
                .build();
    }
}
