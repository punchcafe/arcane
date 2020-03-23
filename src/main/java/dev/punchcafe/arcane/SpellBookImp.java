package dev.punchcafe.arcane;

import java.util.Map;

class SpellBookImp implements SpellBook {

    Map<String, Object> objectMap;

    SpellBookImp(Map<String, Object> objectMap){
        this.objectMap = objectMap;
    }

    @Override
    public <T> T summon(Class<T> clazz) {
        return (T) objectMap.get(clazz.getName());
    }

    @Override
    public <T> T summon(Class<T> clazz, String name) {
        return (T) objectMap.get(clazz);
    }
}
