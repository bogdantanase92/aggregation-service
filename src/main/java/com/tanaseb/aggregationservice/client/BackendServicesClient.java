package com.tanaseb.aggregationservice.client;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import reactor.core.publisher.Mono;

public interface BackendServicesClient {

    Mono<Map<String, List<String>>> callShipmentsService(Set<String> shipmentOrderNumbers);

    Mono<Map<String, String>> callTrackService(Set<String> trackOrderNumbers);

    Mono<Map<String, BigDecimal>> callPricingService(Set<String> pricingCountryCodes);
}
