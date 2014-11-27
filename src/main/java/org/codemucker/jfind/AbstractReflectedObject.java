package org.codemucker.jfind;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.codemucker.jmatch.Matcher;

public abstract class AbstractReflectedObject {

    private final Annotation[] annotations;
    private final int modifiers;

    public AbstractReflectedObject(Annotation[] annotations, int modifiers) {
        super();
        this.annotations = annotations;
        this.modifiers = modifiers;
    }

    public <A extends Annotation> boolean hasAnnotation(Class<A> annotationClass) {
        return getAnnotation(annotationClass) != null;
    }

    public boolean hasAnnotation(String fullName) {
        return getAnnotation(fullName) != null;
    }

    public boolean hasAnnotation(Matcher<Annotation> matcher) {
        return getAnnotation(matcher) != null;
    }

    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        for (Annotation a : annotations) {
            if (a.getClass().equals(annotationClass)) {
                return (A) a;
            }
        }
        return null;
    }

    public Annotation getAnnotation(String fullName) {
        for (Annotation a : annotations) {
            if (a.getClass().getName().equals(fullName)) {
                return a;
            }
        }
        return null;
    }

    public Annotation getAnnotation(Matcher<Annotation> matcher) {
        for (Annotation a : annotations) {
            if (matcher.matches(a)) {
                return a;
            }
        }
        return null;
    }

    public FindResult<Annotation> findAnnotations(Matcher<Annotation> matcher) {
        List<Annotation> found = new ArrayList<>();
        for (Annotation a : annotations) {
            if (matcher.matches(a)) {
                found.add(a);
            }
        }
        return DefaultFindResult.from(found);
    }

    public boolean isAbstract() {
        return isAbstract(true);
    }

    public boolean isNotAbstract() {
        return isAbstract(false);
    }

    public boolean isAbstract(boolean b) {
        return modifier(b, ReflectedModifier.ABSTRACT);
    }

    public boolean isFinal() {
        return isFinal(true);
    }

    public boolean isNotFinal() {
        return isFinal(false);
    }

    public boolean isFinal(boolean b) {
        return modifier(b, ReflectedModifier.FINAL);
    }

    public boolean isNative() {
        return isNative(true);
    }

    public boolean isNotNative() {
        return isNative(false);
    }

    public boolean isNative(boolean b) {
        return modifier(b, ReflectedModifier.NATIVE);
    }

    public boolean isPublic() {
        return isPublic(true);
    }

    public boolean isNotPublic() {
        return isPublic(false);
    }

    public boolean isPublic(boolean b) {
        return modifier(b, ReflectedModifier.PUBLIC);
    }

    public boolean isPrivate() {
        return isPrivate(true);
    }

    public boolean isNotPrivate() {
        return isPrivate(false);
    }

    public boolean isPrivate(boolean b) {
        return modifier(b, ReflectedModifier.PRIVATE);
    }

    public boolean isProtected() {
        return isProtected(true);
    }

    public boolean isNotProtected() {
        return isProtected(false);
    }

    public boolean isProtected(boolean b) {
        return modifier(b, ReflectedModifier.PROTECTED);
    }

    public boolean isSynchronized() {
        return isSynchronized(true);
    }

    public boolean isNotSynchronized() {
        return isSynchronized(false);
    }

    public boolean isSynchronized(boolean b) {
        return modifier(b, ReflectedModifier.SYNCHRONIZED);
    }

    public boolean isTransient() {
        return isTransient(true);
    }

    public boolean isNotTransient() {
        return isTransient(false);
    }

    public boolean isTransient(boolean b) {
        return modifier(b, ReflectedModifier.TRANSIENT);
    }

    public boolean isVolatile() {
        return isVolatile(true);
    }

    public boolean isNotVolatile() {
        return isVolatile(false);
    }

    public boolean isVolatile(boolean b) {
        return modifier(b, ReflectedModifier.VOLATILE);
    }

    public boolean isStrict() {
        return isStrict(true);
    }

    public boolean isNotStrict() {
        return isStrict(false);
    }

    public boolean isStrict(boolean b) {
        return modifier(b, ReflectedModifier.STRICT);
    }

    public boolean isStatic() {
        return isStatic(true);
    }

    public boolean isNotStatic() {
        return isStatic(false);
    }

    public boolean isStatic(boolean b) {
        return modifier(b, ReflectedModifier.STATIC);
    }

    public boolean isModifier(final ReflectedModifier mod) {
        return modifier(true, mod);
    }

    public boolean isNotModifier(final ReflectedModifier mod) {
        return modifier(false, mod);
    }

    public boolean isModifier(final int mod) {
        return modifier(true, ReflectedModifier.from(mod));
    }

    public boolean isNotModifier(final int mod) {
        return modifier(false, ReflectedModifier.from(mod));
    }

    private boolean modifier(final boolean expectMatch, final ReflectedModifier mod) {
        boolean matched = mod.is(modifiers);
        return expectMatch ? matched : !matched;
    }
}
