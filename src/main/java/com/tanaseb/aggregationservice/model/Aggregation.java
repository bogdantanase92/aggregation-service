package com.tanaseb.aggregationservice.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Aggregation {

    private Map<String, List<String>> shipments;
    private Map<String, String> track;
    private Map<String, BigDecimal> pricing;
}
