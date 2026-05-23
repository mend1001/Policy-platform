package com.segurosbolivar.polizas.service.impl;

import com.segurosbolivar.polizas.dto.request.CoreEventRequest;
import com.segurosbolivar.polizas.service.CoreMockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CoreMockServiceImpl implements CoreMockService {

    private static final Logger log = LoggerFactory.getLogger(CoreMockServiceImpl.class);

    @Override
    public void enviarEvento(CoreEventRequest request) {
        log.info("Evento enviado al CORE: evento={}, polizaId={}", request.getEvento(), request.getPolizaId());
    }
}
