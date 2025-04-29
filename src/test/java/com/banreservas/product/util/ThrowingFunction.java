package com.banreservas.product.util;

import java.util.function.Supplier;

@FunctionalInterface
public interface ThrowingFunction<R, E extends Throwable> {
    R apply() throws E;

    static <T, E extends Throwable> Supplier<T> unchecked(ThrowingFunction<T, E> f) {
        return () -> {
            try {
                return f.apply();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }
}
