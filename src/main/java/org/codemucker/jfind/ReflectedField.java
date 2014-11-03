package org.codemucker.jfind;

import java.lang.reflect.Field;

public class ReflectedField extends AbstractReflectedObject {

    private final Field field;

    public static ReflectedField from(Field field) {
        if (field == null) {
            return null;
        }
        return new ReflectedField(field);
    }

    public ReflectedField(Field field) {
        super(field.getAnnotations(),field.getModifiers());
        this.field = field;
    }

    public String getName() {
        return field.getName();
    }

    public Class<?> getType() {
        return field.getType();
    }

    public Field getUnderlying() {
        return field;
    }

}
