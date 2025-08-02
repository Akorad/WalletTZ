package org.Akorad.wallets;


import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadTest {

    private final String BASE_URL = "http://localhost:8080/api/v1/wallets";
    private final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJJdmFuMTIzIiwiaWF0IjoxNzU0MTQxNzIwLCJleHAiOjE3NTQxNDUzMjB9.1qoqNYx-DJySY1-OO5o-Skx884NGGpr1tfP8Kiici2k"; // 🔒 Вставь реальный токен

    @Test
    public void loadTest_deposit1000Requests() throws InterruptedException {
        int numThreads = 50; // Параллельные потоки
        int numRequests = 1000;

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numRequests);

        for (int i = 0; i < numRequests; i++) {
            executor.submit(() -> {
                try {
                    sendDepositRequest();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // Ждём выполнения всех запросов
        executor.shutdown();
    }

    private void sendDepositRequest() {
        RestTemplate restTemplate = new RestTemplate();

        String walletId = "d61cc032-2218-4081-ada2-8dd37e558968"; // 🧠 Укажи реальный UUID кошелька

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(TOKEN); // или headers.add("Authorization", TOKEN);

        String jsonBody = String.format("""
            {
              "walletId": "%s",
              "amount": 1,
              "operationType": "DEPOSIT"
            }
            """, walletId);

        HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(BASE_URL, request, Void.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                System.out.println("❌ Failed: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.out.println("💥 Exception: " + e.getMessage());
        }
    }
}
