package com.TMS.ManagementService.models.dtos.requests;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BookingRequestDto(
        @NotNull UUID bidId,
        @NotNull Integer allocatedTrucks
) {}

