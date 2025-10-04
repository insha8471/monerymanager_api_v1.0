package com.insha.moneymanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrevoEmailService {
    private final WebClient webClient;

    @Value("${BREVO_API_KEY}")
    private String apiKey;

    @Value("${BREVO_FROM_EMAIL}")
    private String fromEmail;

    @Autowired // Constructor injection
    public BrevoEmailService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.brevo.com/v3/smtp/email")
                .build();
    }

    public void sendEmail(String to, String subject, String content) {
        Map<String, Object> body = Map.of(
                "sender", Map.of("email", fromEmail),
                "to", List.of(Map.of("email", to), Map.of("email" , fromEmail)),
                "subject", subject,
                "htmlContent", content
        );

        webClient.post()
                .header("api-key", apiKey)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> System.out.println("Email sent successfully: " + response))
                .doOnError(e -> System.err.println("Email send failed: " + e.getMessage()))
                .subscribe(); // async call
    }
}
