package com.segurosbolivar.polizas.controller;

import com.segurosbolivar.polizas.dto.request.CoreEventRequest;
import com.segurosbolivar.polizas.dto.response.ApiMessages;
import com.segurosbolivar.polizas.dto.response.ApiResponse;
import com.segurosbolivar.polizas.service.CoreMockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/core-mock")
@RequiredArgsConstructor
public class CoreMockController {

    private final CoreMockService coreMockService;

    @PostMapping("/evento")
    public ResponseEntity<ApiResponse<Void>> procesarEvento(@RequestBody CoreEventRequest request) {
        coreMockService.enviarEvento(request);
        return ResponseEntity.ok(ApiResponse.ok(null, ApiMessages.CORE_NOTIFIED));
    }
}
