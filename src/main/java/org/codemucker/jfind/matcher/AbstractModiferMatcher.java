package org.codemucker.jfind.matcher;

import org.codemucker.jfind.JavaModifier;
import org.codemucker.jmatch.AbstractNotNullMatcher;
import org.codemucker.jmatch.Description;
import org.codemucker.jmatch.MatchDiagnostics;
import org.codemucker.jmatch.PropertyMatcher;

/**
 *
 * @param <TSelf> type of the matcher subclass
 * @param <T> type of the object to match against
 */
public abstract class AbstractModiferMatcher<TSelf extends AbstractModiferMatcher<TSelf,T>, T> extends PropertyMatcher<T> {

    @SuppressWarnings("rawtypes")
    public AbstractModiferMatcher(Class beanClass) {
        super(beanClass);
    }

    public TSelf isAbstract() {
        isAbstract(true);
        return self();
    }

    public TSelf isNotAbstract() {
        isAbstract(false);
        return self();
    }

    public TSelf isAbstract(boolean b) {
        modifier(b, JavaModifier.ABSTRACT);
        return self();
    }

    public TSelf isFinal() {
        isFinal(true);
        return self();
    }

    public TSelf isNotFinal() {
        isFinal(false);
        return self();
    }

    public TSelf isFinal(boolean b) {
        modifier(b, JavaModifier.FINAL);
        return self();
    }

    public TSelf isNative() {
        isNative(true);
        return self();
    }

    public TSelf isNotNative() {
        isNative(false);
        return self();
    }

    public TSelf isNative(boolean b) {
        modifier(b, JavaModifier.NATIVE);
        return self();
    }

    public TSelf isPublic() {
        isPublic(true);
        return self();
    }

    public TSelf isNotPublic() {
        isPublic(false);
        return self();
    }

    public TSelf isPublic(boolean b) {
        modifier(b, JavaModifier.PUBLIC);
        return self();
    }

    
    public TSelf isPrivate() {
        isPrivate(true);
        return self();
    }

    public TSelf isNotPrivate() {
        isPrivate(false);
        return self();
    }

    public TSelf isPrivate(boolean b) {
        modifier(b, JavaModifier.PRIVATE);
        return self();
    }

    public TSelf isProtected() {
        isProtected(true);
        return self();
    }

    public TSelf isNotProtected() {
        isProtected(false);
        return self();
    }

    public TSelf isProtected(boolean b) {
        modifier(b, JavaModifier.PROTECTED);
        return self();
    }

    public TSelf isSynchronized() {
        isSynchronized(true);
        return self();
    }

    public TSelf isNotSynchronized() {
        isSynchronized(false);
        return self();
    }

    public TSelf isSynchronized(boolean b) {
        modifier(b, JavaModifier.SYNCHRONIZED);
        return self();
    }

    public TSelf isTransient() {
        isTransient(true);
        return self();
    }

    public TSelf isNotTransient() {
        isTransient(false);
        return self();
    }

    public TSelf isTransient(boolean b) {
        modifier(b, JavaModifier.TRANSIENT);
        return self();
    }

    public TSelf isVolatile() {
        isVolatile(true);
        return self();
    }

    public TSelf isNotVolatile() {
        isVolatile(false);
        return self();
    }

    public TSelf isVolatile(boolean b) {
        modifier(b, JavaModifier.VOLATILE);
        return self();
    }

    public TSelf isStrict() {
        isStrict(true);
        return self();
    }

    public TSelf isNotStrict() {
        isStrict(false);
        return self();
    }

    public TSelf isStrict(boolean b) {
        modifier(b, JavaModifier.STRICT);
        return self();
    }

    public TSelf isStatic() {
        isStatic(true);
        return self();
    }

    public TSelf isNotStatic() {
        isStatic(false);
        return self();
    }

    public TSelf isStatic(boolean b) {
        modifier(b, JavaModifier.STATIC);
        return self();
    }

    public TSelf isModifier(final JavaModifier mod) {
        modifier(true, mod);
        return self();
    }

    public TSelf isNotModifier(final JavaModifier mod) {
        modifier(false, mod);
        return self();
    }

    public TSelf isModifier(final int mod) {
        modifier(true, JavaModifier.from(mod));
        return self();
    }

    public TSelf isNotModifier(final int mod) {
        modifier(false, JavaModifier.from(mod));
        return self();
    }

    private void modifier(final boolean expectMatch, final JavaModifier mod) {
        addMatcher(new AbstractNotNullMatcher<T>() {

            @Override
            protected boolean matchesSafely(T actual, MatchDiagnostics diag) {
                boolean matched = mod.is(getModifier(actual));
                
                return expectMatch?matched:!matched;
            }

            @Override
            public void describeTo(Description desc) {
                desc.text("is " + (expectMatch ? "" : " not") + mod.name());
            }
        });
    }

    protected abstract int getModifier(T instance);

    protected TSelf self() {
        return (TSelf) this;
    }
}
