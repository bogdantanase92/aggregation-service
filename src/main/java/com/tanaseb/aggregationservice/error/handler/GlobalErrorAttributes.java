package com.tanaseb.aggregationservice.error.handler;

import com.tanaseb.aggregationservice.error.CountryCodeException;
import com.tanaseb.aggregationservice.error.OrderNumberException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

record ExceptionRule(Class<?> exceptionClass, HttpStatus status) {

}

@Slf4j
@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    private final List<ExceptionRule> exceptionsRules = List.of(
            new ExceptionRule(OrderNumberException.class, HttpStatus.BAD_REQUEST),
            new ExceptionRule(CountryCodeException.class, HttpStatus.BAD_REQUEST)
    );

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Throwable error = getError(request);
        log.error("Error on request: " + request.path(), error);

        final String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        Optional<ExceptionRule> exceptionRuleOptional = exceptionsRules.stream()
                .map(exceptionRule -> exceptionRule.exceptionClass().isInstance(error) ? exceptionRule : null)
                .filter(Objects::nonNull)
                .findFirst();

        return exceptionRuleOptional.<Map<String, Object>>map(
                        exceptionRule -> Map.of(ErrorAttributesKey.CODE.getKey(), exceptionRule.status().value(),
                                ErrorAttributesKey.MESSAGE.getKey(), error.getMessage(), ErrorAttributesKey.TIME.getKey(),
                                timestamp))
                .orElseGet(() -> Map.of(ErrorAttributesKey.CODE.getKey(), determineHttpStatus(error).value(),
                        ErrorAttributesKey.MESSAGE.getKey(), error.getMessage(), ErrorAttributesKey.TIME.getKey(),
                        timestamp));
    }

    private HttpStatusCode determineHttpStatus(Throwable error) {
        return error instanceof ResponseStatusException err ? err.getStatusCode()
                : MergedAnnotations.from(error.getClass(), MergedAnnotations.SearchStrategy.TYPE_HIERARCHY)
                        .get(ResponseStatus.class).getValue(ErrorAttributesKey.CODE.getKey(), HttpStatus.class)
                        .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

