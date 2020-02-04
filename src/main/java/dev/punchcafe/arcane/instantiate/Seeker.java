package dev.punchcafe.arcane.instantiate;

import dev.punchcafe.arcane.SpellBookPage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Responsible for finding all @SpellBookPage classes on the class path.
 */
public class Seeker {

    public List<Class> allSpellBookPages(String path){
        return null;
        //allSpellBookPages(classFileFinder(path));
    }

    List<Class> allSpellBookPageClasses(List<String> classNames){
        List<Class> classes = new ArrayList<>();

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


    private List<String> classFileFinder(String path) throws IOException {
        List<String> classes = new ArrayList<>();
        Set<Path> paths = Files.list(new File(path).toPath()).collect(Collectors.toSet());
        for (Path p : paths) {
            if ((new File(p.toString())).isDirectory()) {
                classes.addAll(classFileFinder(p.toString()));
            } else {
                if (p.toString().endsWith(".java")) {
                    classes.add(getFullClassName(p.toString()));
                }
            }
        }
        return classes;
    }

    private String getFullClassName(String javaFilePath) throws IOException {
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
