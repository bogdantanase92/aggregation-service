package com.tanaseb.aggregationservice.mapper;

import com.tanaseb.aggregationservice.model.Aggregation;
import org.springframework.util.CollectionUtils;
import reactor.util.function.Tuple3;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class AggregationMapper {

    public static Aggregation buildAggregationDto(
            Tuple3<Map<String, List<String>>, Map<String, String>, Map<String, BigDecimal>> tuple) {
        var aggregation = new Aggregation();
        if (!CollectionUtils.isEmpty(tuple.getT1())) {
            aggregation.setShipments(tuple.getT1());
        }
        if (!CollectionUtils.isEmpty(tuple.getT2())) {
            aggregation.setTrack(tuple.getT2());
        }
        if (!CollectionUtils.isEmpty(tuple.getT3())) {
            aggregation.setPricing(tuple.getT3());
        }

        return aggregation;
    }
}
