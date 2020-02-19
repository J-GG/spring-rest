package fr.jg.springrest.data.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation defines whether a field can be sorted and filtered.
 * If so, it is also possible to specify the name of the field in the corresponding entity.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Pageable {
    /**
     * The matching name in the entity class.
     * By default, the same name as in the domain object.
     * <p>
     * Can be a path such as 'parent.field'.
     *
     * @return The name of the field in the entity class.
     */
    String value() default "";

    /**
     * Whether the field can be sorted out or not.
     *
     * @return True if the field can be sorted out.
     */
    boolean sortable() default true;

    /**
     * Whether the field can be filtered.
     *
     * @return True if the field can be filtered.
     */
    boolean filterable() default true;
}
