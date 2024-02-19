package com.fedex.aggregationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.fedex.aggregationservice.service.AggregationServiceImpl.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

    private static final int MAX_REQUESTS = 5;
    public static final int NO_SECONDS = 5;

    private final Map<String, Set<String>> cache = new ConcurrentHashMap<>();
    private Instant lastTimeCacheUpdated = null;

    public Map<String, Set<String>> cache(Set<String> shipmentOrderNumbers,
                                          Set<String> trackOrderNumbers,
                                          Set<String> pricingCountryCodes) {
        var params = createParams(shipmentOrderNumbers, trackOrderNumbers, pricingCountryCodes);
        var values = updateCache(params);
        log.info("Cached values: {}", cache);

        return values;
    }

    public Map<String, Set<String>> timeBoxAndCache(Set<String> shipmentOrderNumbers,
                                                    Set<String> trackOrderNumbers,
                                                    Set<String> pricingCountryCodes) {
        var values = cache(shipmentOrderNumbers, trackOrderNumbers, pricingCountryCodes);

        var now = Instant.now();
        if (lastTimeCacheUpdated != null && ChronoUnit.SECONDS.between(lastTimeCacheUpdated, now) >= NO_SECONDS) {
            lastTimeCacheUpdated();
            if (CollectionUtils.isEmpty(cache)) {
                return values;
            }
            return cache;
        }

        lastTimeCacheUpdated();
        return values;
    }

    private HashMap<String, Set<String>> createParams(Set<String> shipmentOrderNumbers,
                                                      Set<String> trackOrderNumbers,
                                                      Set<String> pricingCountryCodes) {
        var params = new HashMap<String, Set<String>>();

        if (!CollectionUtils.isEmpty(shipmentOrderNumbers)) {
            params.put(SHIPMENTS, shipmentOrderNumbers);
        }
        if (!CollectionUtils.isEmpty(trackOrderNumbers)) {
            params.put(TRACK, trackOrderNumbers);
        }
        if (!CollectionUtils.isEmpty(pricingCountryCodes)) {
            params.put(PRICING, pricingCountryCodes);
        }

        return params;
    }

    private Map<String, Set<String>> updateCache(Map<String, Set<String>> params) {
        var values = new HashMap<String, Set<String>>();
        values.put(SHIPMENTS, null);
        values.put(TRACK, null);
        values.put(PRICING, null);

        getAPINames().forEach(
                APIName -> {
                    var newValues = params.get(APIName);

                    if (!CollectionUtils.isEmpty(newValues)) {
                        var currentValues = cache.get(APIName);
                        if (newValues.size() >= MAX_REQUESTS) {
                            values.put(APIName, newValues);
                            return;
                        }
                        if (!CollectionUtils.isEmpty(currentValues)) {
                            currentValues.addAll(newValues);
                            if (currentValues.size() >= MAX_REQUESTS) {
                                var firstElements = currentValues.stream()
                                        .limit(MAX_REQUESTS)
                                        .collect(Collectors.toSet());

                                values.put(APIName, firstElements);
                                currentValues.removeAll(firstElements);
                            }
                        } else {
                            cache.put(APIName, newValues);
                        }
                    }
                }
        );

        return values;
    }

    private Set<String> getAPINames() {
        return Set.of(SHIPMENTS, TRACK, PRICING);
    }

    private void lastTimeCacheUpdated() {
        lastTimeCacheUpdated = Instant.now();
        log.info("Cache updated at: {}", lastTimeCacheUpdated);
    }
}
