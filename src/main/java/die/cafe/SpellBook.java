package die.cafe;

public interface SpellBook {

    public static SpellBook threadSpellBook(String classPath){return null;};

    public <T> T summon(Class<T> clazz);
    public <T> T summon(Class<T> clazz, String name);

}
