package com.tanaseb.aggregationservice.service;

import com.tanaseb.aggregationservice.client.BackendServicesClient;
import com.tanaseb.aggregationservice.mapper.AggregationMapper;
import com.tanaseb.aggregationservice.model.Aggregation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregationServiceImpl implements AggregationService {

    public static final String SHIPMENTS = "shipments";
    public static final String TRACK = "track";
    public static final String PRICING = "pricing";

    private final BackendServicesClient backendServicesClient;
    private final ParametersValidator parametersValidator;
    private final CacheServiceImpl cacheService;

    public Mono<Aggregation> aggregateV1(Set<String> shipmentOrderNumbers,
                                         Set<String> trackOrderNumbers,
                                         Set<String> pricingCountryCodes) {
        return Mono.justOrEmpty(parametersValidator.validateParams(shipmentOrderNumbers, trackOrderNumbers, pricingCountryCodes))
                .map(params -> Mono.zip(
                        getShipmentsMono(params.getShipmentOrderNumbers()),
                        getTrackMono(params.getTrackOrderNumbers()),
                        getPricingMono(params.getPricingCountryCodes()))
                )
                .flatMap(mono -> mono)
                .map(AggregationMapper::buildAggregationDto);
    }

    public Mono<Aggregation> aggregateV2(Set<String> shipmentOrderNumbers,
                                         Set<String> trackOrderNumbers,
                                         Set<String> pricingCountryCodes) {
        return Mono.justOrEmpty(parametersValidator.validateParams(shipmentOrderNumbers, trackOrderNumbers, pricingCountryCodes))
                .map(params -> cacheService.cache(
                        params.getShipmentOrderNumbers(), params.getTrackOrderNumbers(), params.getPricingCountryCodes()))
                .map(result -> Mono.zip(
                        getShipmentsMono(result.get(SHIPMENTS)),
                        getTrackMono(result.get(TRACK)),
                        getPricingMono(result.get(PRICING)))
                )
                .flatMap(mono -> mono)
                .map(AggregationMapper::buildAggregationDto);
    }

    public Mono<Aggregation> aggregateV3(Set<String> shipmentOrderNumbers,
                                         Set<String> trackOrderNumbers,
                                         Set<String> pricingCountryCodes) {
        return Mono.justOrEmpty(parametersValidator.validateParams(shipmentOrderNumbers, trackOrderNumbers, pricingCountryCodes))
                .map(params -> cacheService.timeBoxAndCache(
                        params.getShipmentOrderNumbers(), params.getTrackOrderNumbers(), params.getPricingCountryCodes()))
                .map(result -> Mono.zip(
                        getShipmentsMono(result.get(SHIPMENTS)),
                        getTrackMono(result.get(TRACK)),
                        getPricingMono(result.get(PRICING)))
                )
                .flatMap(mono -> mono)
                .map(AggregationMapper::buildAggregationDto);
    }

    private Mono<Map<String, List<String>>> getShipmentsMono(Set<String> shipmentOrderNumbers) {
        return Mono.justOrEmpty(shipmentOrderNumbers)
                .map(backendServicesClient::callShipmentsService)
                .flatMap(mono -> mono)
                .defaultIfEmpty(Map.of());
    }

    private Mono<Map<String, String>> getTrackMono(Set<String> trackOrderNumbers) {
        return Mono.justOrEmpty(trackOrderNumbers)
                .map(backendServicesClient::callTrackService)
                .flatMap(mono -> mono)
                .defaultIfEmpty(Map.of());
    }

    private Mono<Map<String, BigDecimal>> getPricingMono(Set<String> pricingCountryCodes) {
        return Mono.justOrEmpty(pricingCountryCodes)
                .map(backendServicesClient::callPricingService)
                .flatMap(mono -> mono)
                .defaultIfEmpty(Map.of());
    }
}
