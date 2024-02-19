package com.fedex.aggregationservice.api;

import com.fedex.aggregationservice.model.Aggregation;
import com.fedex.aggregationservice.service.AggregationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Set;

import static com.fedex.aggregationservice.service.AggregationServiceImpl.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/aggregation")
public class AggregationController {

    private final AggregationService aggregationService;

    @GetMapping(value = "/v1")
    public Mono<ResponseEntity<Aggregation>> aggregateV1(
            @RequestParam(name = SHIPMENTS, required = false) Set<String> shipmentOrderNumbers,
            @RequestParam(name = TRACK, required = false) Set<String> trackOrderNumbers,
            @RequestParam(name = PRICING, required = false) Set<String> pricingCountryCodes) {
        var aggregateMono = aggregationService.aggregateV1(shipmentOrderNumbers, trackOrderNumbers, pricingCountryCodes);

        return aggregateMono.map(ResponseEntity::ok);
    }

    @GetMapping(value = "/v2")
    public Mono<ResponseEntity<Aggregation>> aggregateV2(
            @RequestParam(name = SHIPMENTS, required = false) Set<String> shipmentOrderNumbers,
            @RequestParam(name = TRACK, required = false) Set<String> trackOrderNumbers,
            @RequestParam(name = PRICING, required = false) Set<String> pricingCountryCodes) {
        var aggregateMono = aggregationService.aggregateV2(shipmentOrderNumbers, trackOrderNumbers, pricingCountryCodes);

        return aggregateMono.map(ResponseEntity::ok);
    }

    @GetMapping(value = "/v3")
    public Mono<ResponseEntity<Aggregation>> aggregateV3(
            @RequestParam(name = SHIPMENTS, required = false) Set<String> shipmentOrderNumbers,
            @RequestParam(name = TRACK, required = false) Set<String> trackOrderNumbers,
            @RequestParam(name = PRICING, required = false) Set<String> pricingCountryCodes) {
        var aggregateMono = aggregationService.aggregateV3(shipmentOrderNumbers, trackOrderNumbers, pricingCountryCodes);

        return aggregateMono.map(ResponseEntity::ok);
    }
}
