package org.codemucker.jfind;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectedAnnotation extends AbstractReflectedObject {

    private static final Class<?>[] NO_ARGS = new Class[] {};
    private static final Object[] NO_ARG_VALS = new Object[] {};

    private final Annotation anon;

    public static ReflectedAnnotation from(Annotation anon) {
        if (anon == null) {
            return null;
        }
        
        return new ReflectedAnnotation(anon);
    }

    public ReflectedAnnotation(Annotation anon) {
        super(anon.annotationType().getAnnotations(),anon.annotationType().getModifiers());
        this.anon = anon;
    }

    public String getName() {
        return anon.annotationType().getName();
    }

    public Class<?> getType() {
        return anon.annotationType();
    }

    public Annotation getUnderlying() {
        return anon;
    }

    public <T extends Annotation> T as(Class<T> anonClass) {
        if (anonClass.isAssignableFrom(anon.getClass())) {
            return (T) anon;
        }
        return null;
    }

    public String getValueForAttribute(String name, String defaultValue) {
        try {
            Method m = anon.getClass().getMethod(name, NO_ARGS);
            Object val = m.invoke(anon, NO_ARG_VALS);
            if (val == null) {
                return defaultValue;
            }
            if (val.toString().length() == 0) {
                return defaultValue;
            }
            return val.toString();
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            return defaultValue;
        }
    }

}
