package com.irctc.irctc_backend.modules.monitoring.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;

@Component
public class CustomServiceHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    public CustomServiceHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        boolean dbHealthy = checkDatabase();
        boolean razorpayHealthy = checkRazorpayConnectivity();

        Health.Builder builder = dbHealthy && razorpayHealthy ? Health.up() : Health.down();

        return builder
                .withDetail("Database", dbHealthy ? "UP" : "DOWN")
                .withDetail("Razorpay API Connectivity", razorpayHealthy ? "UP" : "DOWN (Check Internet / API Endpoint)")
                .build();
    }

    private boolean checkDatabase() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(2);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkRazorpayConnectivity() {
        try {
            // Mock connection test to Razorpay API endpoints
            URL url = new URL("https://api.razorpay.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(2000);
            connection.connect();
            int code = connection.getResponseCode();
            return code > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
