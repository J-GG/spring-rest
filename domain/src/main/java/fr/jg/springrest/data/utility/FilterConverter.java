package fr.jg.springrest.data.utility;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

/**
 * A utility class providing a generic way to get a function to convert a {@link String} into an instance of a provided class.
 */
public class FilterConverter {
    /**
     * Map associating a class with a method converting a {@link String} value to an instance of this class.
     */
    private static final Map<Class<?>, Function<String, ? extends Comparable<?>>> CONVERTERS = new HashMap<>();

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
        CONVERTERS.put(List.class, s -> s);
        CONVERTERS.put(Set.class, s -> s);
    }

    /**
     * Gets an optional function to convert a {@link String} into the provided class.
     *
     * @param clazz The class to convert a {@link String} into.
     * @return A function to convert a {@link String} into the provided class.
     */
    public static Optional<Function<String, ? extends Comparable<?>>> getConverter(final Class<?> clazz) {
        return Optional.ofNullable(FilterConverter.CONVERTERS.get(clazz));
    }
}
