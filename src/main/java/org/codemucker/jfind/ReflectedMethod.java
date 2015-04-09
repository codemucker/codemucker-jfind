package org.codemucker.jfind;

import java.lang.reflect.Method;

public class ReflectedMethod extends AbstractReflectedObject {

    private final Method method;

    public static ReflectedMethod from(Method method) {
        if(method==null){
            return null;
        }
        return new ReflectedMethod(method);
    }

    public ReflectedMethod(Method method) {
        super(method.getAnnotations(),method.getModifiers());
        this.method = method;
    }
    
    public Method getUnderlying(){
        return method;
    }
    
    public String getName(){
    	return method.getName();
    }
}
