package com.TMS.ManagementService.controllers;

import com.TMS.ManagementService.models.dtos.requests.BookingRequestDto;
import com.TMS.ManagementService.models.dtos.responses.BookingResponseDto;
import com.TMS.ManagementService.services.BookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/booking")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    @PostMapping("/register")
    public ResponseEntity<BookingResponseDto> register(@Valid @RequestBody BookingRequestDto bookingRequestDto) throws Exception {
       return ResponseEntity.ok(bookingService.createBooking(bookingRequestDto));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getBooking(@PathVariable @NotNull UUID bookingId) throws Exception {
        return ResponseEntity.ok(bookingService.getBooking(String.valueOf(bookingId)));
    }

    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponseDto> cancelBooking(@PathVariable @NotNull UUID bookingId) throws Exception {
        return ResponseEntity.ok(bookingService.cancelBooking(UUID.fromString(String.valueOf(bookingId))));
    }
}
