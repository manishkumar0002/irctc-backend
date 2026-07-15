package com.irctc.irctc_backend.modules.report.controller;

import com.irctc.irctc_backend.entity.Booking;
import com.irctc.irctc_backend.entity.Passenger;
import com.irctc.irctc_backend.entity.Payment;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/admin/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final EntityManager entityManager;

    // Export Bookings Report as CSV
    @GetMapping("/bookings")
    public ResponseEntity<byte[]> exportBookingsReport() {
        List<Booking> bookings = entityManager.createQuery("SELECT b FROM Booking b", Booking.class).getResultList();

        StringBuilder csv = new StringBuilder();
        csv.append("Booking ID,PNR,User Email,Train Number,Travel Date,Class Type,Seats,Status,Source,Destination,Created At\n");

        for (Booking b : bookings) {
            csv.append(b.getId()).append(",")
               .append(b.getPnr()).append(",")
               .append(b.getUser() != null ? b.getUser().getEmail() : "N/A").append(",")
               .append(b.getTrain() != null ? b.getTrain().getTrainNumber() : "N/A").append(",")
               .append(b.getTravelDate()).append(",")
               .append(b.getClassType()).append(",")
               .append(b.getSeatCount()).append(",")
               .append(b.getStatus()).append(",")
               .append(b.getSourceStationCode()).append(",")
               .append(b.getDestinationStationCode()).append(",")
               .append(b.getCreatedAt()).append("\n");
        }

        byte[] csvData = csv.toString().getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bookings_report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    // Export Revenue Report as CSV
    @GetMapping("/revenue")
    public ResponseEntity<byte[]> exportRevenueReport() {
        List<Payment> payments = entityManager.createQuery("SELECT p FROM Payment p WHERE p.paymentStatus = 'SUCCESS'", Payment.class).getResultList();

        StringBuilder csv = new StringBuilder();
        csv.append("Payment ID,Booking PNR,Amount,Method,Status,Timestamp,Refund Status\n");

        for (Payment p : payments) {
            csv.append(p.getId()).append(",")
               .append(p.getBooking() != null ? p.getBooking().getPnr() : "N/A").append(",")
               .append(p.getAmount()).append(",")
               .append(p.getPaymentMethod()).append(",")
               .append(p.getPaymentStatus()).append(",")
               .append(p.getPaymentTimestamp()).append(",")
               .append(p.getRefundStatus() != null ? p.getRefundStatus() : "NONE").append("\n");
        }

        byte[] csvData = csv.toString().getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=revenue_report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    // Export Passenger List Report as CSV
    @GetMapping("/passengers")
    public ResponseEntity<byte[]> exportPassengerReport() {
        List<Passenger> passengers = entityManager.createQuery("SELECT p FROM Passenger p", Passenger.class).getResultList();

        StringBuilder csv = new StringBuilder();
        csv.append("Passenger ID,Booking PNR,Name,Age,Gender,Seat/Berth Allocation\n");

        for (Passenger p : passengers) {
            csv.append(p.getId()).append(",")
               .append(p.getBooking() != null ? p.getBooking().getPnr() : "N/A").append(",")
               .append(p.getName()).append(",")
               .append(p.getAge()).append(",")
               .append(p.getGender()).append(",")
               .append(p.getSeatNumber() != null ? p.getSeatNumber() : "PENDING").append("\n");
        }

        byte[] csvData = csv.toString().getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=passenger_report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }
}
