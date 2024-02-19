package com.fedex.aggregationservice.api;

import com.fedex.aggregationservice.client.BackendServicesClientImpl;
import com.fedex.aggregationservice.error.handler.GlobalErrorAttributes;
import com.fedex.aggregationservice.service.AggregationServiceImpl;
import com.fedex.aggregationservice.service.CacheServiceImpl;
import com.fedex.aggregationservice.service.ParametersValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.fedex.aggregationservice.service.AggregationServiceImpl.*;
import static com.fedex.aggregationservice.service.CacheServiceImpl.NO_SECONDS;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = AggregationController.class)
@Import({AggregationServiceImpl.class, ParametersValidator.class, CacheServiceImpl.class, GlobalErrorAttributes.class})
class AggregationControllerTest {

    @Autowired
    private WebTestClient webClient;
    @MockBean
    BackendServicesClientImpl backendServicesClient;

    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    @Test
    void aggregateV1() {
        var shipmentOrderNumbers = Set.of("100000000", "100000001");
        var trackOrderNumbers = Set.of("100000000", "100000001");
        var pricingCountryCodes = Set.of("NL", "RO");

        var shipments = createShipments();
        when(backendServicesClient.callShipmentsService(shipmentOrderNumbers)).thenReturn(Mono.just(shipments));

        var track = createTrack();
        when(backendServicesClient.callTrackService(trackOrderNumbers)).thenReturn(Mono.just(track));

        var pricing = createPricing();
        when(backendServicesClient.callPricingService(pricingCountryCodes)).thenReturn(Mono.just(pricing));

        var actual = "{\"shipments\":{\"100000000\":[\"envelope\",\"pallet\"],\"100000001\":[\"pallet\"]},\"track\":{\"100000000\":\"COLLECTED\",\"100000001\":\"COLLECTING\"},\"pricing\":{\"RO\":17.34689282946711,\"NL\":30.72242292141255}}";
        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/aggregation/v1")
                        .queryParam(SHIPMENTS, shipmentOrderNumbers)
                        .queryParam(TRACK, trackOrderNumbers)
                        .queryParam(PRICING, pricingCountryCodes)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(actual);
    }

    @Test
    void aggregateV2() {
        var shipmentOrderNumbers = Set.of("100000000", "100000001");
        var trackOrderNumbers = Set.of("100000000", "100000001");
        var pricingCountryCodes = Set.of("NL", "RO");

        var shipments = createShipments();
        when(backendServicesClient.callShipmentsService(shipmentOrderNumbers)).thenReturn(Mono.just(shipments));

        var track = createTrack();
        when(backendServicesClient.callTrackService(trackOrderNumbers)).thenReturn(Mono.just(track));

        var pricing = createPricing();
        when(backendServicesClient.callPricingService(pricingCountryCodes)).thenReturn(Mono.just(pricing));

        var actual = "{\"shipments\":null,\"track\":null,\"pricing\":null}";
        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/aggregation/v2")
                        .queryParam(SHIPMENTS, shipmentOrderNumbers)
                        .queryParam(TRACK, trackOrderNumbers)
                        .queryParam(PRICING, pricingCountryCodes)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(actual);
    }

    @Test
    void aggregateV3() throws InterruptedException {
        var shipmentOrderNumbers = Set.of("100000000", "100000001");
        var trackOrderNumbers = Set.of("100000000", "100000001");
        var pricingCountryCodes = Set.of("NL", "RO");

        var shipments = createShipments();
        when(backendServicesClient.callShipmentsService(shipmentOrderNumbers)).thenReturn(Mono.just(shipments));

        var track = createTrack();
        when(backendServicesClient.callTrackService(trackOrderNumbers)).thenReturn(Mono.just(track));

        var pricing = createPricing();
        when(backendServicesClient.callPricingService(pricingCountryCodes)).thenReturn(Mono.just(pricing));

        var actual = "{\"shipments\":null,\"track\":null,\"pricing\":null}";
        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/aggregation/v3")
                        .queryParam(SHIPMENTS, shipmentOrderNumbers)
                        .queryParam(TRACK, trackOrderNumbers)
                        .queryParam(PRICING, pricingCountryCodes)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(actual);

        countDownLatch.await(NO_SECONDS, TimeUnit.SECONDS);

        actual = "{\"shipments\":{\"100000000\":[\"envelope\",\"pallet\"],\"100000001\":[\"pallet\"]},\"track\":{\"100000000\":\"COLLECTED\",\"100000001\":\"COLLECTING\"},\"pricing\":{\"RO\":17.34689282946711,\"NL\":30.72242292141255}}";
        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/aggregation/v3")
                        .queryParam(SHIPMENTS, shipmentOrderNumbers)
                        .queryParam(TRACK, trackOrderNumbers)
                        .queryParam(PRICING, pricingCountryCodes)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(actual);
    }

    private Map<String, List<String>> createShipments() {
        return Map.of("100000000", List.of("envelope", "pallet"), "100000001", List.of("pallet"));
    }

    private Map<String, String> createTrack() {
        return Map.of("100000000", "COLLECTED", "100000001", "COLLECTING");
    }

    private Map<String, BigDecimal> createPricing() {
        return Map.of("NL", new BigDecimal("30.72242292141255"), "RO", new BigDecimal("17.34689282946711"));
    }
}