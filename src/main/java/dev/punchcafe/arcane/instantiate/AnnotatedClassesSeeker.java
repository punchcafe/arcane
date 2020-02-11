package dev.punchcafe.arcane.instantiate;

import dev.punchcafe.arcane.SpellBookPage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Responsible for finding all @SpellBookPage classes on the class path.
 */
public class AnnotatedClassesSeeker {

    public static List<Class<?>> allSpellBookPages(String classpath) throws IOException {
        //TODO: ensure this uses an actual classpath
        List<String> paths = List.of(classpath.split(":"));
        Map<String, List<String>> classMap = classFileFinder(classpath, new HashMap<>());
        List<Class<?>> allDiscoveredClasses = loadClasses(classMap);

        return allSpellBookPageClasses(allDiscoveredClasses);
    }

    public static List<Class<?>> allSpellBookPageClasses(List<Class<?>> classNames){
        List<Class<?>> classes = new ArrayList<>();
        for (Class<?> clazz : classNames) {
                for(Annotation annotation : clazz.getAnnotations()){
                    if (annotation.annotationType() == SpellBookPage.class){
                        classes.add(clazz);
                    }
                }
        }
        return classes;
    }


    private static Map<String,List<String>> classFileFinder(String path, Map<String,List<String>> map) throws IOException {
        Set<Path> paths = Files.list(new File(path).toPath()).collect(Collectors.toSet());
        for (Path p : paths) {
            if ((new File(p.toString())).isDirectory()) {
                classFileFinder(p.toString(), map);
            } else {
                if(p.toString().endsWith(".class")) {
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

    private static String removeClassExtension(String classFileName){
        return classFileName.substring(0, classFileName.length()-6);
    }

    private static List<Class<?>> loadClasses(Map<String,List<String>> classDefinitions){
        List<Class<?>> classes = new ArrayList<>();
        final Set<String> allDiscoveredClassNames = classDefinitions.keySet();
        for(String discoveredClassName : allDiscoveredClassNames){
            int numberOfClassesWithName = classDefinitions.get(discoveredClassName).size();
            List<Class<?>> classesWithName = new ArrayList<>();
            for(String singleClassLocation : classDefinitions.get(discoveredClassName)){
                // Loop over all classes with a given class name (but not full name)
                final String[] pathDirectories = singleClassLocation.split("/");
                StringBuilder fullClassNameBuilder = new StringBuilder().append(discoveredClassName);
                for(int i = pathDirectories.length - 2; i >= 0 ; i--){
                    // -2 to ignore class name
                    if(classesWithName.size() >= numberOfClassesWithName){
                        break;
                    }
                    fullClassNameBuilder.insert(0,pathDirectories[i]+".");
                    try {
                        classesWithName.add(Class.forName(fullClassNameBuilder.toString()));
                    } catch (ClassNotFoundException e){
                        System.out.println(e.getStackTrace());
                    }
                }
            }
            classes.addAll(classesWithName);
        }
        return classes;
    }
}
