package dev.punchcafe.arcane;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SpellBookPage {
    String value() default "";
}
