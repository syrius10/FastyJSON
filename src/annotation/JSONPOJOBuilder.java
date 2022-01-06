package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Syrius on 06/01/2022.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface JSONPOJOBuilder {
    /**
     * Property to use for re-defining which zero-argument method
     * is considered the actual "build method": method called after
     * all data has been bound, and the actual instance needs to
     * be instantiated.
     * Default value is "build".
     */
    String buildMethod() default "build";

    /**
     * Property to use for re-defining name prefix to use for
     * auto-detecting "with-methods": methods that are similar to
     * "set-methods" (in that they take an argument), but that
     * may also return the new builder instance to use
     * (which may be 'this', or a new modified builder instance).
     * Note that in addition to this prefix, it is also possible
     * to use {@link annotation.JSONField}
     * annotation to inicate "with-methods".
     * Default value is "with", so that method named "withValue()"
     * would be use for binding JSON property "value" (using type
     * indicated b the argument; or one defined with annotations.
     */
    String withPrefix() default "with";
}
