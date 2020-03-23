package dev.punchcafe.arcane;

import dev.punchcafe.arcane.instantiate.AnnotatedClassesSeeker;
import dev.punchcafe.arcane.instantiate.InstanceGenerator;
import dev.punchcafe.arcane.instantiate.InstanceGeneratorTwo;

import java.io.IOException;

public interface SpellBook {

    static SpellBook threadSpellBook(String classPath) throws IOException {
        final var classes = AnnotatedClassesSeeker.getDependencyMap(classPath);
        //final var classes = AnnotatedClassesSeeker.allSpellBookPages(classPath);
        //final var instanceContainer = InstanceGenerator.generateContainer(classes);
        final var instanceContainer = InstanceGeneratorTwo.generateContainer(classes);
        return new SpellBookImp(instanceContainer);
    }

    static SpellBook threadSpellBook(String classPath, RuneMap map) {return null;}

    <T> T summon(Class<T> clazz);
    <T> T summon(Class<T> clazz, String name);

}
