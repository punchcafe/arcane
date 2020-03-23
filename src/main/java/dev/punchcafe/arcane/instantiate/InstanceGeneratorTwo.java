package dev.punchcafe.arcane.instantiate;

import dev.punchcafe.arcane.SpellName;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstanceGeneratorTwo<T> {

    public static Map<String, Object> generateContainer(Map<String, AnnotatedClassesSeeker.SpellBookParts> nameMap){
        Map<String, Object> returnMap = new HashMap<>();
        for(String spellBookPageName : nameMap.keySet()){
            new InstanceGeneratorTwo<>(nameMap.get(spellBookPageName), nameMap, returnMap).generate();
        }
        return returnMap;
    }

    private AnnotatedClassesSeeker.SpellBookParts reagents;
    private Map<String, AnnotatedClassesSeeker.SpellBookParts> nameMap;
    private Map<String, Object> container;

    public InstanceGeneratorTwo(AnnotatedClassesSeeker.SpellBookParts spellBookParts,
                             Map<String, AnnotatedClassesSeeker.SpellBookParts> nameMap,
                             Map<String, Object> container){
        this.reagents = spellBookParts;
        this.nameMap = nameMap;
        this.container = container;
    }

    public Object generate(){

        if(container.get(reagents.name) != null){
            return container.get(reagents.name);
        }

        if(reagents.getInitializer().getParameterCount() == 0){
            try {
                if (reagents.getInitializer() instanceof Constructor) {
                    Constructor castConstructor = (Constructor) reagents.getInitializer();
                    final Object obj = castConstructor.newInstance();
                    container.put(reagents.getName(), obj);
                    return obj;
                } else if (reagents.getInitializer() instanceof Method) {
                    Method castMethod = (Method) reagents.getInitializer();
                    final Object obj=  castMethod.invoke(null);
                    container.put(reagents.getName(), obj);
                    return obj;
                }
            } catch (Exception ex){
                System.out.println("nope!");
            }
        }

        List<Parameter> parameters = Arrays.asList(reagents.getInitializer().getParameters());
        Object[] toInsert = new Object[reagents.getInitializer().getParameterCount()];
        for(int i = 0; i < parameters.size(); i++){
            Parameter parameter = parameters.get(i);
            StringBuilder parameterName = new StringBuilder().append(parameter.getType().getName());
            if(parameter.isAnnotationPresent(SpellName.class)){
                parameterName.append(":");
                parameterName.append(parameter.getAnnotation(SpellName.class).name());
            }
            if(container.get(parameterName.toString()) != null ){
                toInsert[i] = container.get(parameterName.toString());
            } else {
                final AnnotatedClassesSeeker.SpellBookParts parts = nameMap.get(parameterName.toString());
                final Object objectToInsert = (new InstanceGeneratorTwo<>(parts, nameMap, container)).generate();
                toInsert[i] = objectToInsert;
                System.out.println("lol");
            }
        }
        try {
            if (this.reagents.getInitializer() instanceof Method) {
                Method castMethod = (Method) this.reagents.getInitializer();
                final Object obj = castMethod.invoke(null, toInsert);
                container.put(reagents.name,obj);
                return obj;
            } else if (this.reagents.getInitializer() instanceof Constructor) {
                Constructor castConstructor = (Constructor) this.reagents.getInitializer();
                final Object obj  = castConstructor.newInstance(toInsert);
                container.put(reagents.name,obj);
                return obj;
            }
        } catch (Exception ex){
            System.out.println("unlucky!");
        }
        return null;
    }

}
