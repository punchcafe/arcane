package dev.punchcafe.arcane.instantiate;

import dev.punchcafe.arcane.Incantation;
import dev.punchcafe.arcane.SpellBookPage;
import dev.punchcafe.arcane.SpellName;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Responsible for finding all @SpellBookPage classes on the class path.
 */
public class AnnotatedClassesSeeker {

    static class SpellBookParts {
        Class<?> clazz;
        Executable initializer;
        String name;

        public SpellBookParts(Class<?> clazz, Executable initializer, String name) {
            this.clazz = clazz;
            this.initializer = initializer;
            this.name = name;
        }

        public Class<?> getClazz(){
            return this.clazz;
        }

        public Executable getInitializer(){
            return this.initializer;
        }

        public String getName(){
            return this.name;
        }
    }

    public static Map<String, SpellBookParts> dependencyMap(List<Class<?>> allClasses) {
        final Map<String, SpellBookParts> resultMap = new HashMap<>();
        final List<Class<?>> allSpellBookPageClasses = allSpellBookPageClasses(allClasses);
        for (Class<?> clazz : allSpellBookPageClasses) {
            // Get constructor
            final Constructor initializer = Arrays.stream(clazz.getConstructors()).filter(
                    constructor -> constructor.isAnnotationPresent(Incantation.class)
            ).findFirst().orElseThrow(IllegalArgumentException::new);
            resultMap.put(clazz.getName(), new SpellBookParts(clazz, initializer, clazz.getName()));
            // find any factory methods for other beans
            List<Method> factoryMethods =  Arrays.stream(clazz.getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(SpellName.class))
                    .collect(Collectors.toList());
            for(Method factoryMethod : factoryMethods){
                SpellName annotation = factoryMethod.getAnnotation(SpellName.class);
                Class<?> returnType = factoryMethod.getReturnType();
                final String name = returnType.getName()+":"+annotation.name();
                resultMap.put(name, new SpellBookParts(returnType, factoryMethod, name));
            }
        }
        return resultMap;
        // TODO: use this map to recursively generate when bootstrapping.
    }

    public static Map<String, SpellBookParts> getDependencyMap(String classpath ) throws IOException {
        //TODO: ensure this uses an actual classpath
        List<String> paths = List.of(classpath.split(":"));
        Map<String, List<String>> classMap = new HashMap<>();
        classFileFinder(classpath, classMap);
        List<Class<?>> allDiscoveredClasses = loadClasses(classMap);

        return dependencyMap(allDiscoveredClasses);
    }

    public static List<Class<?>> allSpellBookPages(String classpath) throws IOException {
        //TODO: ensure this uses an actual classpath
        List<String> paths = List.of(classpath.split(":"));
        Map<String, List<String>> classMap = new HashMap<>();
        classFileFinder(classpath, classMap);
        List<Class<?>> allDiscoveredClasses = loadClasses(classMap);

        return allSpellBookPageClasses(allDiscoveredClasses);
    }

    static List<Class<?>> allSpellBookPageClasses(List<Class<?>> classNames) {
        List<Class<?>> classes = new ArrayList<>();
        for (Class<?> clazz : classNames) {
            for (Annotation annotation : clazz.getAnnotations()) {
                if (annotation.annotationType() == SpellBookPage.class) {
                    classes.add(clazz);
                }
            }
        }
        return classes;
    }


    private static Map<String, List<String>> classFileFinder(String path, Map<String, List<String>> map) throws IOException {
        Set<Path> paths = Files.list(new File(path).toPath()).collect(Collectors.toSet());
        for (Path p : paths) {
            if ((new File(p.toString())).isDirectory()) {
                classFileFinder(p.toString(), map);
            } else {
                if (p.toString().endsWith(".class")) {
                    List<String> appended = Optional.ofNullable(map.get(removeClassExtension(p.getFileName().toString()))).orElse(new ArrayList<>());
                    appended.add(p.toString());
                    map.put(removeClassExtension(p.getFileName().toString()), appended);
                }
            }
        }

        // TODO: enable loading of Default Package classes
        // TODO: extract .class extensions to use static constants to determine length
        return map;
    }

    private static String removeClassExtension(String classFileName) {
        return classFileName.substring(0, classFileName.length() - 6);
    }

    private static List<Class<?>> loadClasses(Map<String, List<String>> classDefinitions) {
        List<Class<?>> classes = new ArrayList<>();
        final Set<String> allDiscoveredClassNames = classDefinitions.keySet();
        for (String discoveredClassName : allDiscoveredClassNames) {
            int numberOfClassesWithName = classDefinitions.get(discoveredClassName).size();
            List<Class<?>> classesWithName = new ArrayList<>();
            for (String singleClassLocation : classDefinitions.get(discoveredClassName)) {
                // Loop over all classes with a given class name (but not full name)
                final String[] pathDirectories = singleClassLocation.split("/");
                StringBuilder fullClassNameBuilder = new StringBuilder().append(discoveredClassName);
                for (int i = pathDirectories.length - 2; i >= 0; i--) {
                    // -2 to ignore class name
                    if (classesWithName.size() >= numberOfClassesWithName) {
                        break;
                    }
                    fullClassNameBuilder.insert(0, pathDirectories[i] + ".");
                    try {
                        classesWithName.add(Class.forName(fullClassNameBuilder.toString()));
                    } catch (ClassNotFoundException e) {
                        System.out.println(e.getStackTrace());
                    }
                }
            }
            classes.addAll(classesWithName);
        }
        return classes;
    }
}
