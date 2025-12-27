package com.TMS.ManagementService.models.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TransporterRequestDto(
       @NotBlank String companyName,
       @NotNull Double rating,
       @NotEmpty List<TruckCapacityDto> availableTrucks
) {}

