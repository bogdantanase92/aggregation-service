package com.fedex.aggregationservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class BackendServicesClientImpl implements BackendServicesClient {

    @Value("${backend.services.shipments.url}")
    private String shipmentsUrl;
    @Value("${backend.services.track.url}")
    private String trackUrl;
    @Value("${backend.services.pricing.url}")
    private String pricingUrl;

    private final WebClient webClient;

    @Override
    public Mono<Map<String, List<String>>> callShipmentsService(Set<String> shipmentOrderNumbers) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(shipmentsUrl)
                        .queryParam("q", shipmentOrderNumbers)
                        .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, List<String>>>() {
                })
                .doOnSuccess(result -> log.info(
                        "[Shipments-Service] Request has been sent for order numbers: " + shipmentOrderNumbers))
                .doOnError(throwable -> log.error("[Shipments-service] Request has been failed", throwable))
                .onErrorReturn(Map.of());
    }

    @Override
    public Mono<Map<String, String>> callTrackService(Set<String> trackOrderNumbers) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(trackUrl)
                        .queryParam("q", trackOrderNumbers)
                        .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
                })
                .doOnSuccess(result -> log.info(
                        "[Track-Service] Request has been sent for order numbers: " + trackOrderNumbers))
                .doOnError(throwable -> log.error("[Track-service] Request has been failed", throwable))
                .onErrorReturn(Map.of());
    }

    @Override
    public Mono<Map<String, BigDecimal>> callPricingService(Set<String> pricingCountryCodes) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(pricingUrl)
                        .queryParam("q", pricingCountryCodes)
                        .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, BigDecimal>>() {
                })
                .doOnSuccess(result -> log.info(
                        "[Pricing-Service] Request has been sent for country codes: " + pricingCountryCodes))
                .doOnError(throwable -> log.error("[Pricing-service] Request has been failed", throwable))
                .onErrorReturn(Map.of());
    }
}
