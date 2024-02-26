package com.tanaseb.aggregationservice.service;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.tanaseb.aggregationservice.service.AggregationServiceImpl.*;
import static com.tanaseb.aggregationservice.service.CacheServiceImpl.NO_SECONDS;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CacheServiceImplTest {

    private final CacheService cacheService = new CacheServiceImpl();

    @Test
    void cacheValues_whenNot5Threshold_returnNullValues() {
        var shipmentOrderNumbers = Set.of("100000000", "100000001");
        var trackOrderNumbers = Set.of("100000000", "100000001");
        var pricingCountryCodes = Set.of("NL", "RO");

        var expected = new HashMap<String, Set<String>>();
        expected.put(SHIPMENTS, null);
        expected.put(TRACK, null);
        expected.put(PRICING, null);
        var actual = cacheService.cache(shipmentOrderNumbers, trackOrderNumbers, pricingCountryCodes);
        assertEquals(expected, actual);
    }

    @Test
    void cacheValues_when5ThresholdForShipment_return5Shipment() {
        var shipmentOrderNumbers = Set.of("100000000", "100000001", "100000002", "100000003", "100000004");
        var trackOrderNumbers = Set.of("100000000", "100000001");
        var pricingCountryCodes = Set.of("NL", "RO");

        var expected = new HashMap<String, Set<String>>();
        expected.put(SHIPMENTS, shipmentOrderNumbers);
        expected.put(TRACK, null);
        expected.put(PRICING, null);
        var actual = cacheService.cache(shipmentOrderNumbers, trackOrderNumbers, pricingCountryCodes);
        assertEquals(expected, actual);
    }

    @Test
    void cacheValues_when5ThresholdForShipmentAndTrackAndPricing_return5ShipmentAnd5TrackAnd5Pricing() {
        var shipmentOrderNumbers = Set.of("100000000", "100000001", "100000002", "100000003", "100000004");
        var trackOrderNumbers = Set.of("100000000", "100000001", "100000002", "100000003", "100000004");
        var pricingCountryCodes = Set.of("NL", "RO", "US", "UK", "CN");

        var expected = new HashMap<String, Set<String>>();
        expected.put(SHIPMENTS, shipmentOrderNumbers);
        expected.put(TRACK, trackOrderNumbers);
        expected.put(PRICING, pricingCountryCodes);
        var actual = cacheService.cache(shipmentOrderNumbers, trackOrderNumbers, pricingCountryCodes);
        assertEquals(expected, actual);
    }

    @Test
    void cacheValues_when2ThresholdForShipment_then3ThresholdForShipment_return5Shipment() {
        var batch1ShipmentOrderNumbers = new HashSet<String>();
        batch1ShipmentOrderNumbers.add("100000000");
        batch1ShipmentOrderNumbers.add("100000001");
        var batch1TrackOrderNumbers = new HashSet<String>();
        batch1TrackOrderNumbers.add("100000000");
        batch1TrackOrderNumbers.add("100000001");
        var batch1PricingCountryCodes = new HashSet<String>();
        batch1PricingCountryCodes.add("NL");
        batch1PricingCountryCodes.add("RO");

        var batch1Expected = new HashMap<String, Set<String>>();
        batch1Expected.put(SHIPMENTS, null);
        batch1Expected.put(TRACK, null);
        batch1Expected.put(PRICING, null);
        var batch1Actual = cacheService.cache(batch1ShipmentOrderNumbers, batch1TrackOrderNumbers, batch1PricingCountryCodes);
        assertEquals(batch1Expected, batch1Actual);

        var batch2ShipmentOrderNumbers = new HashSet<String>();
        batch2ShipmentOrderNumbers.add("100000002");
        batch2ShipmentOrderNumbers.add("100000003");
        batch2ShipmentOrderNumbers.add("100000004");
        var batch2TrackOrderNumbers = new HashSet<String>();
        batch2TrackOrderNumbers.add("100000000");
        batch2TrackOrderNumbers.add("100000001");
        var batch2PricingCountryCodes = new HashSet<String>();
        batch2PricingCountryCodes.add("NL");
        batch2PricingCountryCodes.add("RO");

        var batch2Expected = new HashMap<String, Set<String>>();
        batch2Expected.put(SHIPMENTS, Set.of("100000000", "100000001", "100000002", "100000003", "100000004"));
        batch2Expected.put(TRACK, null);
        batch2Expected.put(PRICING, null);
        var batch2Actual = cacheService.cache(batch2ShipmentOrderNumbers, batch2TrackOrderNumbers, batch2PricingCountryCodes);
        assertEquals(batch2Expected, batch2Actual);
    }

    @Test
    void cacheValues_when2ThresholdForShipmentAndTrackAndPricing_then3ThresholdForShipmentAndTrackAndPricing_return5ShipmentAnd5TrackAnd5Pricing() {
        var batch1ShipmentOrderNumbers = new HashSet<String>();
        batch1ShipmentOrderNumbers.add("100000000");
        batch1ShipmentOrderNumbers.add("100000001");
        var batch1TrackOrderNumbers = new HashSet<String>();
        batch1TrackOrderNumbers.add("100000000");
        batch1TrackOrderNumbers.add("100000001");
        var batch1PricingCountryCodes = new HashSet<String>();
        batch1PricingCountryCodes.add("NL");
        batch1PricingCountryCodes.add("RO");

        var batch1Expected = new HashMap<String, Set<String>>();
        batch1Expected.put(SHIPMENTS, null);
        batch1Expected.put(TRACK, null);
        batch1Expected.put(PRICING, null);
        var batch1Actual = cacheService.cache(batch1ShipmentOrderNumbers, batch1TrackOrderNumbers, batch1PricingCountryCodes);
        assertEquals(batch1Expected, batch1Actual);

        var batch2ShipmentOrderNumbers = new HashSet<String>();
        batch2ShipmentOrderNumbers.add("100000002");
        batch2ShipmentOrderNumbers.add("100000003");
        batch2ShipmentOrderNumbers.add("100000004");
        var batch2TrackOrderNumbers = new HashSet<String>();
        batch2TrackOrderNumbers.add("100000002");
        batch2TrackOrderNumbers.add("100000003");
        batch2TrackOrderNumbers.add("100000004");
        var batch2PricingCountryCodes = new HashSet<String>();
        batch2PricingCountryCodes.add("US");
        batch2PricingCountryCodes.add("UK");
        batch2PricingCountryCodes.add("CN");

        var batch2Expected = new HashMap<String, Set<String>>();
        batch2Expected.put(SHIPMENTS, Set.of("100000000", "100000001", "100000002", "100000003", "100000004"));
        batch2Expected.put(TRACK, Set.of("100000000", "100000001", "100000002", "100000003", "100000004"));
        batch2Expected.put(PRICING, Set.of("NL", "RO", "US", "UK", "CN"));
        var batch2Actual = cacheService.cache(batch2ShipmentOrderNumbers, batch2TrackOrderNumbers, batch2PricingCountryCodes);
        assertEquals(batch2Expected, batch2Actual);
    }

    @Test
    void timeBoxAndCacheValues_when2ThresholdForShipmentAndTrackAndPricing_thenWait5Secs_return2ShipmentAnd2TrackAnd2Pricing() {
        var shipmentOrderNumbers = new HashSet<String>();
        shipmentOrderNumbers.add("100000000");
        shipmentOrderNumbers.add("100000001");
        var trackOrderNumbers = new HashSet<String>();
        trackOrderNumbers.add("100000000");
        trackOrderNumbers.add("100000001");
        var pricingCountryCodes = new HashSet<String>();
        pricingCountryCodes.add("NL");
        pricingCountryCodes.add("RO");

        cacheService.timeBoxAndCache(shipmentOrderNumbers, trackOrderNumbers, pricingCountryCodes);

        var expected = new HashMap<String, Set<String>>();
        expected.put(SHIPMENTS, Set.of("100000000", "100000001"));
        expected.put(TRACK, Set.of("100000000", "100000001"));
        expected.put(PRICING, Set.of("NL", "RO"));

        Awaitility.await().pollDelay(Duration.ofSeconds(NO_SECONDS))
                .until(() -> cacheService.timeBoxAndCache(
                        shipmentOrderNumbers, trackOrderNumbers, pricingCountryCodes), equalTo(expected));
    }

    @Test
    void timeBoxAndCacheValues_when5ThresholdForShipment_thenWait5Secs_when5ThresholdForShipment_return5Shipment() {
        var shipmentOrderNumbers = new HashSet<String>();
        shipmentOrderNumbers.add("100000000");
        shipmentOrderNumbers.add("100000001");
        shipmentOrderNumbers.add("100000002");
        shipmentOrderNumbers.add("100000003");
        shipmentOrderNumbers.add("100000004");

        var expected = new HashMap<String, Set<String>>();
        expected.put(SHIPMENTS, Set.of("100000000", "100000001", "100000002", "100000003", "100000004"));
        expected.put(TRACK, null);
        expected.put(PRICING, null);
        var actual = cacheService.timeBoxAndCache(shipmentOrderNumbers, null, null);
        assertEquals(expected, actual);


        Awaitility.await().pollDelay(Duration.ofSeconds(NO_SECONDS))
                .until(() -> cacheService.timeBoxAndCache(
                        shipmentOrderNumbers, null, null), equalTo(expected));
    }

    @Test
    void timeBoxAndCacheValues_when5ThresholdForShipment_return5Shipment() {
        var shipmentOrderNumbers = new HashSet<String>();
        shipmentOrderNumbers.add("100000000");
        shipmentOrderNumbers.add("100000001");
        shipmentOrderNumbers.add("100000002");
        shipmentOrderNumbers.add("100000003");
        shipmentOrderNumbers.add("100000004");

        var expected = new HashMap<String, Set<String>>();
        expected.put(SHIPMENTS, Set.of("100000000", "100000001", "100000002", "100000003", "100000004"));
        expected.put(TRACK, null);
        expected.put(PRICING, null);
        var actual = cacheService.timeBoxAndCache(shipmentOrderNumbers, null, null);
        assertEquals(expected, actual);
    }
}