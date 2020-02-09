package dev.punchcafe.arcane;

import java.util.Map;

class SpellBookImp implements SpellBook {

    Map<Class<?>, Object> objectMap;

    SpellBookImp(Map<Class<?>, Object> objectMap){
        this.objectMap = objectMap;
    }

    @Override
    public <T> T summon(Class<T> clazz) {
        return (T) objectMap.get(clazz);
    }

    @Override
    public <T> T summon(Class<T> clazz, String name) {
        return (T) objectMap.get(clazz);
    }
}
