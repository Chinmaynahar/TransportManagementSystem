package com.TMS.ManagementService.controllers;

import com.TMS.ManagementService.models.dtos.requests.LoadRequestDto;
import com.TMS.ManagementService.models.dtos.responses.BidResponseDto;
import com.TMS.ManagementService.models.dtos.responses.LoadResponseDto;
import com.TMS.ManagementService.services.LoadService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/load")
public class LoadController {
    private final LoadService loadService;

    public LoadController(LoadService loadService) {
        this.loadService = loadService;
    }

    @PostMapping("/register")
    public ResponseEntity<LoadResponseDto> registerLoad(@Valid @RequestBody LoadRequestDto loadRequestDto){
        return ResponseEntity.ok(loadService.register(loadRequestDto));
    }

    @GetMapping
    public ResponseEntity<List<LoadResponseDto>> getLoads(
            @RequestParam(required = false) String shipperId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ){
        return ResponseEntity.ok(loadService.getLoads(shipperId,status,page,size));
    }

    @GetMapping("/{loadId}")
    public ResponseEntity<LoadResponseDto> getLoadWithBids(@PathVariable @NotNull UUID loadId){
        return ResponseEntity.ok(loadService.getLoadWithBids(loadId));
    }

    @PatchMapping("/{loadId}/cancel")
    public ResponseEntity<LoadResponseDto> cancelLoad(@PathVariable @NotNull UUID loadId) throws Exception {
        return ResponseEntity.ok(loadService.cancelLoad(loadId));
    }

    @GetMapping("/{loadId}/best-bids")
    public ResponseEntity<List<BidResponseDto>> getBestBids(@PathVariable @NotNull UUID loadId) throws Exception {
        return ResponseEntity.ok(loadService.getBestBids(loadId));
    }
}
