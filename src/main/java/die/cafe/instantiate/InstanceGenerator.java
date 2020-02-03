package die.cafe.instantiate;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

    // TODO: remove the need for external dependency tree creation ?
    // Or create a static method which takes a list of classes and crafts dependency map?

    public InstanceGenerator(Class<T> generateSubject,
                             Map<Class<?>, List<Class<?>>> dependencyMap,
                             Map<Class<?>, Object> instanceGeneratorCache) {
        this.instanceClass = generateSubject;
        this.dependencyList = dependencyMap.get(generateSubject);
        this.instanceGeneratorCache = instanceGeneratorCache;
        this.dependencyMap = dependencyMap;
    }

    public static Map<Class<?>, Object> generateContainer(List<Class> classes){
        // TODO: Implement app logic here.
        return null;
    }

    public T generate() {
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
