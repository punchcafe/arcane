package dev.punchcafe.arcane.instantiate;

import dev.punchcafe.arcane.Incantation;
import dev.punchcafe.arcane.SpellBookPage;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class InstanceGenerator<T> {
    private Class<T> instanceClass;
    private List<Class<?>> dependencyList;
    private Map<Class<?>, List<Class<?>>> dependencyMap;
    // Map that keeps track of already generated instances to avoid duplication.
    // Initially uses class as an identifier, but eventually will use names.
    private Map<Class<?>, Object> instanceGeneratorCache;

    private InstanceGenerator(Class<T> generateSubject,
                             Map<Class<?>, List<Class<?>>> dependencyMap,
                             Map<Class<?>, Object> instanceGeneratorCache) {
        this.instanceClass = generateSubject;
        this.dependencyList = dependencyMap.get(generateSubject);
        this.instanceGeneratorCache = instanceGeneratorCache;
        this.dependencyMap = dependencyMap;
    }

    public static Map<Class<?>, Object> generateContainer(List<Class<?>> classes) {
        final List<Class<?>> spellBookClasses = classes.stream().filter(clazz -> clazz.isAnnotationPresent(SpellBookPage.class)).collect(Collectors.toList());
        HashMap<Class<?>, List<Class<?>>> dependencyTempMap = new HashMap<>();
        for (Class<?> clazz : spellBookClasses) {
            // For now ignore more than one incantation
            Optional<Constructor<?>> injecteeConstructor = Arrays.stream(clazz.getConstructors()).filter(constructor -> constructor.isAnnotationPresent(Incantation.class)).findAny();
            injecteeConstructor.ifPresent(constructor -> dependencyTempMap.put(clazz, List.of(constructor.getParameterTypes())));
        }
        final var dependencyMap = Map.copyOf(dependencyTempMap);
        final var instanceCache = new HashMap<Class<?>, Object>();
        for(Class<?> clazz : spellBookClasses){
            if(instanceCache.get(clazz) == null) {
                instanceCache.put(clazz, new InstanceGenerator(clazz, dependencyMap, instanceCache).generate());
            }
        }
        return instanceCache;
    }

    private T generate() {
        final var cachedInstance = instanceGeneratorCache.get(instanceClass);
        if (cachedInstance != null) {
            return (T) cachedInstance;
        }
        final Object[] dependencies = dependencyList.stream().map(clazz -> (new InstanceGenerator(clazz, dependencyMap, instanceGeneratorCache)).generate()).collect(Collectors.toUnmodifiableList()).toArray();
        final Constructor<T> constructor;
        try {
            constructor = instanceClass.getConstructor(dependencyList.toArray(new Class[dependencyList.size()]));
            final var instance = constructor.newInstance(dependencies);
            instanceGeneratorCache.put(instanceClass, instance);
            return instance;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
