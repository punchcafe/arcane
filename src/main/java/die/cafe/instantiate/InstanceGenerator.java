package die.cafe.instantiate;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InstanceGenerator<T> {
    private Class<T> instanceClass;
    private List<Class<?>> dependencyList;
    private Map<Class<?>, List<Class<?>>> dependencyMap;
    // Map that keeps track of already generated instances to avoid duplication.
    // Initially uses class as an identifier, but eventually will use names.
    private Map<Class<?>, Object> instanceGeneratorCache;

    public InstanceGenerator(Class<T> generateSubject,
                             Map<Class<?>, List<Class<?>>> dependencyMap,
                             Map<Class<?>, Object> instanceGeneratorCache) {
        this.instanceClass = generateSubject;
        this.dependencyList = dependencyMap.get(generateSubject);
        this.instanceGeneratorCache = instanceGeneratorCache;
        this.dependencyMap = dependencyMap;
    }

    public T generate() {
        final var cachedInstance = instanceGeneratorCache.get(instanceClass);
        if (cachedInstance != null) {
            return (T) cachedInstance;
        }
        final Object[] dependencies = dependencyList.stream().map(clazz -> (new InstanceGenerator(clazz, dependencyMap, instanceGeneratorCache)).generate()).collect(Collectors.toUnmodifiableList()).toArray();
        final Constructor<T> constructor;
        try {
            constructor = instanceClass.getConstructor(dependencyList.toArray(new Class[]{}));
            return constructor.newInstance(dependencies);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
