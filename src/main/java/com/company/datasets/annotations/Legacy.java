package com.company.datasets.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
* Annotation for any legacy content that is not relevant anymore, but old data still uses it
* not equivalent to @Deprecated and does not indicate intent to remove
 */
@Retention(RetentionPolicy.SOURCE)
public @interface Legacy {

    String message() default "";

    /**
     * game version since which this is Legacy
     */
    String legacySince();

}
