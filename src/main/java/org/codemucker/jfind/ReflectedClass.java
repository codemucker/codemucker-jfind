package org.codemucker.jfind;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.codemucker.jmatch.Matcher;

public class ReflectedClass extends AbstractReflectedObject {

    private final Class<?> type;

    public static ReflectedClass from(Class<?> type) {
        if (type == null) {
            return null;
        }
        return new ReflectedClass(type);
    }

    public ReflectedClass(Class<?> type) {
        super(type.getAnnotations(), type.getModifiers());
        this.type = type;
    }

    public Class<?> getUnderlying() {
        return type;
    }

    public boolean hasMethodMatching(Matcher<Method> matcher) {
        Class<?> t = type;
        List<String> visitedMethods = new ArrayList<>();// ignore overridden
                                                        // methods
        while (t != null && t != Object.class) {
            for (Method m : t.getDeclaredMethods()) {
                if (m.isBridge()) {
                    continue;
                }
                String sig = m.toGenericString();
                if (!visitedMethods.contains(sig)) {
                    if (matcher.matches(m)) {
                        return true;
                    }
                    ;
                    visitedMethods.add(sig);
                }
            }
            t = t.getSuperclass();
        }
        return false;
    }

    public FindResult<Method> findMethodsMatching(Matcher<Method> matcher) {
        List<Method> found = new ArrayList<>();
        Class<?> t = type;
        List<String> visitedMethods = new ArrayList<>();
        while (t != null && t != Object.class) {
            for (Method m : t.getDeclaredMethods()) {
                if (m.isBridge()) {
                    continue;
                }
                String sig = m.toGenericString();
                if (!visitedMethods.contains(sig)) {
                    visitedMethods.add(sig);
                    if (matcher.matches(m)) {
                        found.add(m);
                    }
                    ;
                }
            }
            t = t.getSuperclass();
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
