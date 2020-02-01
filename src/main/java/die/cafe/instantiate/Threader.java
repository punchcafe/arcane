package die.cafe.instantiate;

import java.util.HashMap;
import java.util.List;

public class Threader {

    HashMap<Class, Class[]> spellBookPageDependencyMap(List<Class> spellbookPageClasses){
        HashMap<Class, Class[]> dependencyMap = new HashMap<>();
        for (Class clazz : spellbookPageClasses) {
            dependencyMap.put(clazz, clazz.getConstructors()[0].getParameterTypes());
        }
        return dependencyMap;
    }
}
