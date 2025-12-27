package com.TMS.ManagementService.controllers;


import com.TMS.ManagementService.models.dtos.requests.BidRequestDto;
import com.TMS.ManagementService.models.dtos.responses.BidResponseDto;
import com.TMS.ManagementService.models.enums;
import com.TMS.ManagementService.services.BidService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bid")
public class BidController {
    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @PostMapping("/register")
    public ResponseEntity<BidResponseDto> submitBid(@Valid @RequestBody BidRequestDto req) throws Exception {
        return ResponseEntity.ok(bidService.register(req));
    }

    @GetMapping
    public ResponseEntity<List<BidResponseDto>> filterBids (
            @RequestParam(required = false) UUID loadId,
            @RequestParam(required = false) UUID transporterId,
            @RequestParam(required = false) enums.BidStatus status
    ) throws Exception{
        return ResponseEntity.ok(bidService.filterBids(loadId, transporterId, status));
    }

    @GetMapping("/{bidId}")
    public ResponseEntity<BidResponseDto> getBid(@PathVariable @NotNull UUID bidId) throws Exception {
        return ResponseEntity.ok(bidService.getBid(bidId));
    }

    @PatchMapping("/{bidId}/reject")
    public ResponseEntity<BidResponseDto> rejectBid(@PathVariable @NotNull UUID bidId) throws Exception {
        return ResponseEntity.ok(bidService.rejectBid(bidId));
    }
}
