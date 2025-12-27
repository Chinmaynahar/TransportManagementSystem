package com.TMS.ManagementService.models.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TruckCapacityDto(
       @NotBlank String truckType,
       @NotNull Integer count
) {}

