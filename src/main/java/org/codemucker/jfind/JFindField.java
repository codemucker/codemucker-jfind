package org.codemucker.jfind;

import java.lang.reflect.Field;

public class JFindField extends AbstractAccessibleObject {

    private final Field field;

    public static JFindField from(Field field) {
        if (field == null) {
            return null;
        }
        return new JFindField(field);
    }

    public JFindField(Field field) {
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
