package com.irctc.irctc_backend.modules.ticket.controller;

import com.irctc.irctc_backend.modules.ticket.service.TicketPrintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/v1/tickets")
@RequiredArgsConstructor
public class TicketPrintController {

    private final TicketPrintService ticketPrintService;

    @GetMapping(value = "/{bookingId}/print", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> printTicket(@PathVariable Long bookingId) {
        String html = ticketPrintService.generateHtmlTicket(bookingId);
        return ResponseEntity.ok(html);
    }
}
