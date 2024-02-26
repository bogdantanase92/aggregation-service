package com.tanaseb.aggregationservice.service;

import com.tanaseb.aggregationservice.client.BackendServicesClient;
import com.tanaseb.aggregationservice.model.Aggregation;
import com.tanaseb.aggregationservice.model.Params;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.tanaseb.aggregationservice.service.AggregationServiceImpl.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AggregationServiceImplTest {

    @InjectMocks
    private AggregationServiceImpl aggregationService;
    @Mock
    private BackendServicesClient backendServicesClient;
    @Mock
    private ParametersValidator parametersValidator;
    @Mock
    private CacheServiceImpl cacheService;

    @Test
    void aggregateV1() {
        var shipmentOrderNumbers = Set.of("100000000", "100000001");
        var trackOrderNumbers = Set.of("100000000", "100000001");
        var pricingCountryCodes = Set.of("NL", "RO");

        when(parametersValidator.validateParams(shipmentOrderNumbers, trackOrderNumbers, pricingCountryCodes))
                .thenReturn(new Params(shipmentOrderNumbers, trackOrderNumbers, pricingCountryCodes));

        var shipments = createShipments();
        when(backendServicesClient.callShipmentsService(shipmentOrderNumbers)).thenReturn(Mono.just(shipments));

        var track = createTrack();
        when(backendServicesClient.callTrackService(trackOrderNumbers)).thenReturn(Mono.just(track));

        var pricing = createPricing();
        when(backendServicesClient.callPricingService(pricingCountryCodes)).thenReturn(Mono.just(pricing));

        var actual = aggregationService.aggregateV1(shipmentOrderNumbers, trackOrderNumbers, pricingCountryCodes);
        StepVerifier.create(actual.log())
                .expectNext(new Aggregation(shipments, track, pricing))
                .verifyComplete();
    }

    @Test
    void aggregateV2() {
        var shipmentOrderNumbers = Set.of("100000000", "100000001");
        var trackOrderNumbers = Set.of("100000000", "100000001");
        var pricingCountryCodes = Set.of("NL", "RO");

        when(parametersValidator.validateParams(shipmentOrderNumbers, trackOrderNumbers, pricingCountryCodes))
                .thenReturn(new Params(shipmentOrderNumbers, trackOrderNumbers, pricingCountryCodes));

        var cachedValues = new HashMap<String, Set<String>>();
        cachedValues.put(SHIPMENTS, null);
        cachedValues.put(TRACK, null);
        cachedValues.put(PRICING, null);
        when(cacheService.cache(shipmentOrderNumbers, trackOrderNumbers, pricingCountryCodes)).thenReturn(cachedValues);

        var actual = aggregationService.aggregateV2(shipmentOrderNumbers, trackOrderNumbers, pricingCountryCodes);
        StepVerifier.create(actual.log())
                .expectNext(new Aggregation())
                .verifyComplete();
    }

    @Test
    void aggregateV3() {
        var shipmentOrderNumbers = Set.of("100000000", "100000001");
        var trackOrderNumbers = Set.of("100000000", "100000001");
        var pricingCountryCodes = Set.of("NL", "RO");

        when(parametersValidator.validateParams(shipmentOrderNumbers, trackOrderNumbers, pricingCountryCodes))
                .thenReturn(new Params(shipmentOrderNumbers, trackOrderNumbers, pricingCountryCodes));

        var cachedValues = new HashMap<String, Set<String>>();
        cachedValues.put(SHIPMENTS, null);
        cachedValues.put(TRACK, null);
        cachedValues.put(PRICING, null);
        when(cacheService.timeBoxAndCache(shipmentOrderNumbers, trackOrderNumbers, pricingCountryCodes)).thenReturn(cachedValues);

        var actual = aggregationService.aggregateV3(shipmentOrderNumbers, trackOrderNumbers, pricingCountryCodes);
        StepVerifier.create(actual.log())
                .expectNext(new Aggregation())
                .verifyComplete();
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