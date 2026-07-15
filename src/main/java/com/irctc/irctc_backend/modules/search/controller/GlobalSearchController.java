package com.irctc.irctc_backend.modules.search.controller;

import com.irctc.irctc_backend.entity.Booking;
import com.irctc.irctc_backend.entity.Payment;
import com.irctc.irctc_backend.entity.Train;
import com.irctc.irctc_backend.entity.User;
import com.irctc.irctc_backend.modules.common.dto.ApiResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class GlobalSearchController {

    private final EntityManager entityManager;

    // Admin Global Search
    @GetMapping("/global")
    public ResponseEntity<ApiResponse<Map<String, Object>>> globalSearch(@RequestParam String query) {
        Map<String, Object> results = new HashMap<>();

        // Search Users by Name or Email
        List<User> users = entityManager.createQuery(
                "SELECT u FROM User u WHERE LOWER(u.name) LIKE :q OR LOWER(u.email) LIKE :q", User.class)
                .setParameter("q", "%" + query.toLowerCase() + "%")
                .setMaxResults(10)
                .getResultList();
        results.put("users", users);

        // Search Bookings by PNR
        List<Booking> bookings = entityManager.createQuery(
                "SELECT b FROM Booking b WHERE LOWER(b.pnr) LIKE :q", Booking.class)
                .setParameter("q", "%" + query.toLowerCase() + "%")
                .setMaxResults(10)
                .getResultList();
        results.put("bookings", bookings);

        // Search Payments by Gateway ID
        List<Payment> payments = entityManager.createQuery(
                "SELECT p FROM Payment p WHERE LOWER(p.gatewayOrderId) LIKE :q OR LOWER(p.gatewayPaymentId) LIKE :q", Payment.class)
                .setParameter("q", "%" + query.toLowerCase() + "%")
                .setMaxResults(10)
                .getResultList();
        results.put("payments", payments);

        return ResponseEntity.ok(ApiResponse.success(results));
    }

    // Advanced Train Search with Filters
    @GetMapping("/trains")
    public ResponseEntity<ApiResponse<List<Train>>> searchTrainsWithFilters(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam(required = false) String trainType, // rajdhani, express, mail
            @RequestParam(required = false) Double maxFare,
            @RequestParam(required = false) String coachClass // SL, 3A, 2A, 1A
    ) {
        // Query to find trains joining routes and stops
        StringBuilder jpql = new StringBuilder(
                "SELECT DISTINCT t FROM Train t " +
                "JOIN TrainRoute r ON r.train = t " +
                "JOIN TrainStop s1 ON s1.route = r " +
                "JOIN TrainStop s2 ON s2.route = r " +
                "WHERE s1.station.code = :source AND s2.station.code = :destination " +
                "AND s1.stopOrder < s2.stopOrder AND s1.halt = true AND s2.halt = true"
        );

        if (trainType != null) {
            jpql.append(" AND LOWER(t.trainName) LIKE :trainType");
        }

        TypedQuery<Train> query = entityManager.createQuery(jpql.toString(), Train.class);
        query.setParameter("source", source);
        query.setParameter("destination", destination);

        if (trainType != null) {
            query.setParameter("trainType", "%" + trainType.toLowerCase() + "%");
        }

        List<Train> trains = query.getResultList();
        return ResponseEntity.ok(ApiResponse.success(trains));
    }
}
