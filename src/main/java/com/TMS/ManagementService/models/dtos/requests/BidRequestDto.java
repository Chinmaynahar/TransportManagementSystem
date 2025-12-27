package com.TMS.ManagementService.models.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BidRequestDto(
       @NotNull UUID loadId,
       @NotNull UUID transporterId,
       @NotNull Double proposedRate,
       @NotNull Integer trucksOffered
) {}

