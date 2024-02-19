package com.fedex.aggregationservice.service;

import com.fedex.aggregationservice.model.Aggregation;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface AggregationService {

    Mono<Aggregation> aggregateV1(Set<String> shipmentOrderNumbers,
                                  Set<String> trackOrderNumbers,
                                  Set<String> pricingCountryCodes);

    Mono<Aggregation> aggregateV2(Set<String> shipmentOrderNumbers,
                                  Set<String> trackOrderNumbers,
                                  Set<String> pricingCountryCodes);

    Mono<Aggregation> aggregateV3(Set<String> shipmentOrderNumbers,
                                  Set<String> trackOrderNumbers,
                                  Set<String> pricingCountryCodes);
}
