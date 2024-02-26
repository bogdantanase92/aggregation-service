package com.tanaseb.aggregationservice.service;

import com.tanaseb.aggregationservice.error.CountryCodeException;
import com.tanaseb.aggregationservice.error.OrderNumberException;
import com.tanaseb.aggregationservice.model.Params;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ParametersValidator {

    private static final int ORDER_NUMBER_LENGTH = 9;
    private static final int COUNTRY_CODE_LENGTH = 2;
    private static final String ALPHABET_PATTERN = "[a-zA-Z]+";
    public static final String NOT_INTEGER_MESSAGE = "Order number [%s] must be an integer";
    public static final String NOT_9_DIGITS_MESSAGE = "Order number [%s] must have 9 digits";
    public static final String NOT_2_CHARS = "Country code [%s] must have 2 characters";
    public static final String NOT_ONLY_ALPHABET_CHARS = "Country code [%s] must have only alphabet characters";

    public Params validateParams(Set<String> shipmentOrderNumbers, Set<String> trackOrderNumbers, Set<String> pricingCountryCodes) {
        var validatedShipmentOrderNumbers = validateOrderNumbers(shipmentOrderNumbers);
        var validatedTrackOrderNumbers = validateOrderNumbers(trackOrderNumbers);
        var validatedPricingCountryCodes = validateCountryCodes(pricingCountryCodes);

        return new Params(validatedShipmentOrderNumbers, validatedTrackOrderNumbers, validatedPricingCountryCodes);
    }

    private Set<String> validateOrderNumbers(Set<String> orderNumbers) {
        if (!CollectionUtils.isEmpty(orderNumbers)) {
            orderNumbers.forEach(orderNumber -> {
                try {
                    Integer.parseInt(orderNumber);
                } catch (NumberFormatException e) {
                    throw new OrderNumberException(String.format(NOT_INTEGER_MESSAGE, orderNumber));
                }
                if (orderNumber.length() != ORDER_NUMBER_LENGTH) {
                    throw new OrderNumberException(String.format(NOT_9_DIGITS_MESSAGE, orderNumber));
                }
            });
        }
        return orderNumbers;
    }

    private Set<String> validateCountryCodes(Set<String> countryCodes) {
        if (!CollectionUtils.isEmpty(countryCodes)) {
            countryCodes.forEach(countryCode -> {
                if (countryCode.length() != COUNTRY_CODE_LENGTH) {
                    throw new CountryCodeException(
                            String.format(NOT_2_CHARS, countryCode));
                }
                if (!countryCode.matches(ALPHABET_PATTERN)) {
                    throw new CountryCodeException(
                            String.format(NOT_ONLY_ALPHABET_CHARS, countryCode));
                }
            });
            return countryCodes.stream()
                    .map(String::toUpperCase)
                    .collect(Collectors.toSet());
        }
        return null;
    }
}
