package com.fedex.aggregationservice.service;

import com.fedex.aggregationservice.error.CountryCodeException;
import com.fedex.aggregationservice.error.OrderNumberException;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.fedex.aggregationservice.service.ParametersValidator.*;
import static org.junit.jupiter.api.Assertions.*;

class ParametersValidatorTest {

    private final ParametersValidator validator = new ParametersValidator();

    @Test
    void validateOrderNumbers() {
        var orderNumbers = Set.of("100000000", "100000001");

        assertDoesNotThrow(() -> validator.validateParams(orderNumbers, orderNumbers, null));
    }

    @Test
    void validateOrderNumbers_NoInteger() {
        var invalidOrderNumber = "noIntegerNumber";
        var orderNumbers = Set.of(invalidOrderNumber);

        var expected = String.format(NOT_INTEGER_MESSAGE, invalidOrderNumber);
        var actual = assertThrows(OrderNumberException.class,
                () -> validator.validateParams(orderNumbers, orderNumbers, null));
        assertEquals(expected, actual.getMessage());
    }

    @Test
    void validateOrderNumbers_Not9Digits() {
        var invalidOrderNumber = "100";
        var orderNumbers = Set.of(invalidOrderNumber);

        var expected = String.format(NOT_9_DIGITS_MESSAGE, invalidOrderNumber);
        var actual = assertThrows(OrderNumberException.class,
                () -> validator.validateParams(orderNumbers, orderNumbers, null));
        assertEquals(expected, actual.getMessage());
    }

    @Test
    void validateCountryCodes() {
        var countryCodes = Set.of("NL", "RO");

        assertDoesNotThrow(() -> validator.validateParams(null, null, countryCodes));
    }

    @Test
    void validateCountryCodes_Not2Chars() {
        var invalidCountryCode = "ABC";
        var countryCodes = Set.of(invalidCountryCode);

        var expected = String.format(NOT_2_CHARS, invalidCountryCode);
        var actual = assertThrows(CountryCodeException.class,
                () -> validator.validateParams(null, null, countryCodes));
        assertEquals(expected, actual.getMessage());
    }

    @Test
    void validateCountryCodes_NotOnlyAlphabetChars() {
        var invalidCountryCode = "A1";
        var countryCodes = Set.of(invalidCountryCode);

        var expected = String.format(NOT_ONLY_ALPHABET_CHARS, invalidCountryCode);
        var actual = assertThrows(CountryCodeException.class,
                () -> validator.validateParams(null, null, countryCodes));
        assertEquals(expected, actual.getMessage());
    }
}