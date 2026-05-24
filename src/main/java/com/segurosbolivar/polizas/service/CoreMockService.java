package com.segurosbolivar.polizas.service;

import com.segurosbolivar.polizas.dto.request.CoreEventRequest;
import com.segurosbolivar.polizas.model.Policy;

public interface CoreMockService {

    void sendEvent(CoreEventRequest request);

    void notifyCore(Policy policy, String eventType);
}
