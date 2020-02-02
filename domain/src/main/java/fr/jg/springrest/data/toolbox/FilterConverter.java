package fr.jg.springrest.data.toolbox;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class FilterConverter {
    private static final Map<Class<?>, Function<String, ? extends Comparable>> CONVERTERS = new HashMap<>();

    static {
        CONVERTERS.put(String.class, s -> s);
        CONVERTERS.put(UUID.class, UUID::fromString);
        CONVERTERS.put(Long.class, Long::valueOf);
        CONVERTERS.put(Integer.class, Integer::valueOf);
        CONVERTERS.put(Double.class, Double::valueOf);
        CONVERTERS.put(Float.class, Float::valueOf);
        CONVERTERS.put(Boolean.class, Boolean::parseBoolean);
        CONVERTERS.put(BigDecimal.class, BigDecimal::new);
        CONVERTERS.put(LocalDate.class, LocalDate::parse);
    }

    public static Optional<Function<String, ? extends Comparable>> getConverter(final Class<?> clazz) {
        return Optional.ofNullable(FilterConverter.CONVERTERS.get(clazz));
    }
}
