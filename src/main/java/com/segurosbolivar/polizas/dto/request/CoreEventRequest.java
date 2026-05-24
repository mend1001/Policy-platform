package com.segurosbolivar.polizas.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoreEventRequest {

    @NotBlank(message = "Event type is required")
    @Size(max = 50, message = "Event type must not exceed 50 characters")
    @Pattern(regexp = "^[A-Z_]+$", message = "Event type must be uppercase letters and underscores only")
    private String event;

    @NotNull(message = "Policy ID is required")
    private UUID policyId;
}
