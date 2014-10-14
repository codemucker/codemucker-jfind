package org.codemucker.jfind;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.codemucker.jmatch.Matcher;

public class JFindClass extends AbstractAccessibleObject {

    private final Class<?> type;

    public static JFindClass from(Class<?> type) {
        if (type == null) {
            return null;
        }
        return new JFindClass(type);
    }

    public JFindClass(Class<?> type) {
        super(type.getAnnotations(),type.getModifiers());
        this.type = type;
    }

    public Class<?> getUnderlying(){
        return type;
    }
    
    public boolean hasMethodMatching(Matcher<Method> matcher) {
        for (Method m : type.getDeclaredMethods()) {
            if (matcher.matches(m)) {
                return true;
            }
        }
        return false;
    }

    public FindResult<Method> findMethodsMatching(Matcher<Method> matcher) {
        List<Method> found = new ArrayList<>();
        for (Method m : type.getDeclaredMethods()) {
            if (matcher.matches(m)) {
                found.add(m);
            }
        }
        return DefaultFindResult.from(found);
    }

    public boolean hasFieldsMatching(Matcher<Field> matcher) {
        for (Field f : type.getDeclaredFields()) {
            if (matcher.matches(f)) {
                return true;
            }
        }
        return false;
    }

    public FindResult<Field> findFieldsMatching(Matcher<Field> matcher) {
        List<Field> found = new ArrayList<>();
        for (Field f : type.getDeclaredFields()) {
            if (matcher.matches(f)) {
                found.add(f);
            }
        }
        return DefaultFindResult.from(found);
    }

}
