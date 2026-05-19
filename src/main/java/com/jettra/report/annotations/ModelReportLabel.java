package com.jettra.report.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define fields representing report labels with specific section placement and alignment.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface ModelReportLabel {
    String label() default "";

    enum Section {
        HEADER, FOOTER, LASTPAGE, FIRSTPAGE
    }

    enum Orientation {
        LEFT, CENTER, RIGHT
    }

    Section section() default Section.HEADER;
    Orientation orientation() default Orientation.LEFT;
}
