package com.fedex.aggregationservice.service;

import java.util.Map;
import java.util.Set;

public interface CacheService {

    Map<String, Set<String>> cache(Set<String> shipmentOrderNumbers,
                                   Set<String> trackOrderNumbers,
                                   Set<String> pricingCountryCodes);

    Map<String, Set<String>> timeBoxAndCache(Set<String> shipmentOrderNumbers,
                                             Set<String> trackOrderNumbers,
                                             Set<String> pricingCountryCodes);
}
