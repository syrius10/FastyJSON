package annotation;

import com.sun.org.apache.xalan.internal.utils.FeatureManager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Syrius on 06/01/2022.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface JSONField {
    /**
     * config encode/decode ordinal
     */
    int ordinal() default 0;
    String name() default "";
    String format() default "";
    boolean serialize() default true;
    boolean deserialize() default true;
    SerializerFeature[] serializeFeatures() default {};
    Feature[] parseFeatures() default {};
    String label() default "";
    boolean jsonDirect() default Void.class;
    /**
     * Serializer class to use for serializing associated value.
     */
    Class<?> serializeUsing() default Void.class;

    /**
     * Deserializer class to use for deserializing associated value.
     */
    Class<?> deserializeUsing() default Void.class;

    /**
     * @return te alternative names of the field when it is deserialized
     */
    String[] alternateNames() default {};

    boolean unwrapped() default false;

    /**
     * Only support object
     */
    String defaultValue() default "";
}
