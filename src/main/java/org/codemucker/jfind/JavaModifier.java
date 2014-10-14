package org.codemucker.jfind;

import java.lang.reflect.Modifier;

public enum  JavaModifier {

    ABSTRACT(Modifier.ABSTRACT),
    FINAL(Modifier.FINAL),
    INTERFACE(Modifier.INTERFACE),
    NATIVE(Modifier.NATIVE),
    PRIVATE(Modifier.PRIVATE),
    PROTECTED(Modifier.PROTECTED),
    PUBLIC(Modifier.PUBLIC),
    STATIC(Modifier.STATIC),
    STRICT(Modifier.STRICT),
    SYNCHRONIZED(Modifier.SYNCHRONIZED),
    TRANSIENT(Modifier.TRANSIENT),
    VOLATILE(Modifier.VOLATILE);
    
    public final int mod;
    
    private JavaModifier(int mod){
        this.mod = mod;
    }
    
    public boolean is(int mod) {
        return (this.mod & mod) != 0;
    }
    
    public static String toString(int mod){
        return from(mod).toString();
    }
    
    public static JavaModifier from(int mod){
        for(JavaModifier m:values()){
            if(m.is(mod)){
                return m;
            }
        }
        throw new IllegalArgumentException("unrecognized modifier " + mod);
    }
}
