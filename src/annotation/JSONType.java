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
public @interface JSONType {

    boolean asm() default true;
    String[] orders() default {};
    String[] includes() default {};
    String[] ignores() default {};
    SerializerFeature[] serializeFeatures() default {};
    Feature[] parseFeatures() default {};
    boolean alphabetic() default true;
    Class<?> mappingTo() default Void.class;
    Class<?> builder() default Void.class;
    String typeName() default "";
    String typeKey() default "";
    Class<?>[] seeAlso() default {};
    Class<?> serializer() default Void.class;
    Class<?> deserializer() default Void.class;
    boolean serializeEnumAsJavaBean() default false;
    PropertyNamingStrategy naming() default PropertyNamingStrategy.NeverUseThisValueExceptDefaultValue;
    Class<? extends SerializeFilter>[] serializeFilters() default {};
    Class<? extends ParserConfig.AutoTypeCheckHandler> autoTypeCheckHandler() default ParserConfig.AutoTypeCheckHandler.class;
}
