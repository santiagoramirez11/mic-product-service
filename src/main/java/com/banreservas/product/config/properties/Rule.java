package com.banreservas.product.config.properties;

import java.util.Set;

public record Rule(String path, String method, Set<String> roles) {
}
