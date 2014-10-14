package org.codemucker.jfind;

import java.lang.reflect.Method;

public class JFindMethod extends AbstractAccessibleObject {

    private final Method method;

    public static JFindMethod from(Method method) {
        if(method==null){
            return null;
        }
        return new JFindMethod(method);
    }

    public JFindMethod(Method method) {
        super(method.getAnnotations(),method.getModifiers());
        this.method = method;
    }
    
    public Method getUnderlying(){
        return method;
    }
}
