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
        return allSpellBookPageClasses(classFileFinder(classpath));
    }

    public static List<Class<?>> allSpellBookPageClasses(List<String> classNames){
        List<Class<?>> classes = new ArrayList<>();
        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                for(Annotation annotation : clazz.getAnnotations()){
                    if (annotation.annotationType() == SpellBookPage.class){
                        classes.add(clazz);
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return classes;
    }


    private static List<String> classFileFinder(String path) throws IOException {
        /**
         * Instead start with the .class path and expand/contract depending on file
         * names until the class has been successfully loaded.
         * Alternatively, attempt to use recursion to find the package. Or at least
         * always start at the file above, and work outwards. If it's none of those then it's the default
         * package.
         */
        List<String> classes = new ArrayList<>();
        Set<Path> paths = Files.list(new File(path).toPath()).collect(Collectors.toSet());
        Map<String,List<String>> classDefinitions = new HashMap<>();
        for (Path p : paths) {
            if ((new File(p.toString())).isDirectory()) {
                classes.addAll(classFileFinder(p.toString()));
            } else {
                if (p.toString().endsWith(".java")) {
                    classes.add(getFullClassName(p.toString()));
                }
                if(p.toString().endsWith(".class")) {
                    List<String> appended = Optional.ofNullable(classDefinitions.get(p.getFileName().toString())).orElse(new ArrayList<>());
                    appended.add(p.toString());
                    classDefinitions.put(p.getFileName().toString(), appended);
                }
            }
        }
        // TODO: use the class definitions list with l-1 index and work backwards when one.
        // TODO: when multiple, work until the amount of loaded classes is equal to the list.
        return classes;
    }

    private static String getFullClassName(String javaFilePath) throws IOException {
        File file = new File(javaFilePath);

        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        st = br.readLine();
        while (!st.contains("package")) {
            st = br.readLine();
            if (st == null) {
                return null;
            }
        }
        String packageName = st.trim().substring(8, st.length() - 1);
        String className = file.getName().substring(0, file.getName().length() - 5);
        return (new StringBuilder()).append(packageName).append(".").append(className).toString();
    }
}
