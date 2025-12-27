package com.TMS.ManagementService.models.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoadRequestDto(
       @NotBlank String shipperId,
       @NotBlank String loadingCity,
       @NotBlank String unloadingCity,
       @NotBlank String loadingDate,
       @NotBlank String productType,
       @NotNull Double weight,
       @NotBlank String weightUnit,
       @NotBlank String truckType,
       @NotNull Integer noOfTrucks
) {}

