package com.irctc.irctc_backend.service;

import com.irctc.irctc_backend.config.RailwayApiConfig;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RailwayLiveStatusService {

    private final RailwayApiConfig config;
    private final OkHttpClient client = new OkHttpClient();

    public String getLiveTrainStatus(String trainNumber) {

        String url = config.getBaseUrl()
                + "/api/trains/v1/train/status?trainNumber=" + trainNumber;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("x-rapidapi-key", config.getApiKey())
                .addHeader("x-rapidapi-host", config.getApiHost())
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new RuntimeException(
                        "Railway API error: " + response.code()
                );
            }

            return response.body().string();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to fetch live train status", e
            );
        }
    }
}
