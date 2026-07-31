package uk.ac.cf._5.group14.One_To_One.Operations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExclusiveScheduledJob {

    String value();

    String lockAtMostFor() default "PT30M";
}
