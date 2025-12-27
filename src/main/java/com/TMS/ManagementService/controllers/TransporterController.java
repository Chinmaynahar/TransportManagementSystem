package com.TMS.ManagementService.controllers;

import com.TMS.ManagementService.models.dtos.requests.TransporterRequestDto;
import com.TMS.ManagementService.models.dtos.requests.TruckCapacityDto;
import com.TMS.ManagementService.models.dtos.responses.TransporterResponseDto;
import com.TMS.ManagementService.services.TransporterService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/transporter")
public class TransporterController {
    private final TransporterService transporterService;

    public TransporterController(TransporterService transporterService) {
        this.transporterService = transporterService;
    }

    @PostMapping("/register")
    public ResponseEntity<TransporterResponseDto> register(@Valid @RequestBody TransporterRequestDto transporterRequestDto){
        return ResponseEntity.ok(transporterService.register(transporterRequestDto));
    }

    @GetMapping("/{transporterId}")
    public ResponseEntity<TransporterResponseDto> get(@PathVariable @NotNull UUID transporterId) throws Exception {
        return ResponseEntity.ok(transporterService.getTransporter(String.valueOf(transporterId)));    }


    @PutMapping("/{transporterId}/trucks")
    public ResponseEntity<TransporterResponseDto> updateTrucks(
            @PathVariable @NotNull UUID transporterId,
            @RequestBody @Valid TruckCapacityDto dto) throws Exception {
        return ResponseEntity.ok(transporterService.updateTrucks(String.valueOf(transporterId), dto));
    }
}
