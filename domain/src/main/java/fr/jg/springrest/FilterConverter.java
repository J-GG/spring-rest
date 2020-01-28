package fr.jg.springrest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class FilterConverter {
    private static final Map<Class<?>, Function<String, ? extends Comparable>> converters = new HashMap<>();

    static {
        converters.put(String.class, s -> s);
        converters.put(UUID.class, UUID::fromString);
        converters.put(Long.class, Long::valueOf);
        converters.put(Integer.class, Integer::valueOf);
        converters.put(Double.class, Double::valueOf);
        converters.put(Float.class, Float::valueOf);
        converters.put(BigDecimal.class, BigDecimal::new);
        converters.put(LocalDate.class, LocalDate::parse);
    }

    public static Optional<Function<String, ? extends Comparable>> getConverter(final Class<?> clazz) {
        return Optional.ofNullable(FilterConverter.converters.get(clazz));
    }
}
