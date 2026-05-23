package com.segurosbolivar.polizas.controller;

import com.segurosbolivar.polizas.dto.request.CoreEventRequest;
import com.segurosbolivar.polizas.service.CoreMockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/core-mock")
@RequiredArgsConstructor
public class CoreMockController {

    private final CoreMockService coreMockService;

    @PostMapping("/evento")
    public ResponseEntity<Map<String, String>> procesarEvento(@RequestBody CoreEventRequest request) {
        coreMockService.enviarEvento(request);
        return ResponseEntity.ok(Map.of("mensaje", "Evento registrado en CORE"));
    }
}
