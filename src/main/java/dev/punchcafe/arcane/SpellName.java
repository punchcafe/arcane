package dev.punchcafe.arcane;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows for refining of dependency injection by specifying a name.
 * The id of the instantiated summoned will have the name Class:spellName
 * When on parameter, indicates the one to look up
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface SpellName {
    String name();
}
