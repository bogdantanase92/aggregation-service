package com.tanaseb.aggregationservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Params {

    private Set<String> shipmentOrderNumbers;
    private Set<String> trackOrderNumbers;
    private Set<String> pricingCountryCodes;
}
