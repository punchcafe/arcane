package dev.punchcafe.arcane;

public interface SpellBook {

    static SpellBook threadSpellBook(String classPath){return null;};

    <T> T summon(Class<T> clazz);
    <T> T summon(Class<T> clazz, String name);

}
