package com.banreservas.product.util;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.nio.file.Files.readAllLines;
import static java.nio.file.Paths.get;

public final class JsonUtils {

    private static final ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();

    public static <T> T toObject(String json, Class<T> clazz) {
        return ThrowingFunction.unchecked(() -> objectMapper.readValue(json, clazz)).get();
    }

    public static <T> T toObject(String json, TypeReference<T> typeReference) {
        return ThrowingFunction.unchecked(() -> objectMapper.readValue(json, typeReference)).get();
    }

    public static String readFile(String resource) {
        return ThrowingFunction.unchecked(() -> readAllLines(get(resourceOf(resource)))).get()
                .stream()
                .map(String::trim)
                .collect(Collectors.joining());
    }

    private static URI resourceOf(String resource) throws URISyntaxException {
        return Objects.requireNonNull(MethodHandles.lookup().lookupClass()
                .getResource(resource)).toURI();
    }
}