package com.segurosbolivar.polizas.service;

import com.segurosbolivar.polizas.dto.request.CoreEventRequest;

public interface CoreMockService {

    void enviarEvento(CoreEventRequest request);
}
