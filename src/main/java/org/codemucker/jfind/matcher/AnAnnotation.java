package org.codemucker.jfind.matcher;

import java.lang.annotation.Annotation;

import org.codemucker.jmatch.AString;
import org.codemucker.jmatch.AbstractNotNullMatcher;
import org.codemucker.jmatch.Description;
import org.codemucker.jmatch.MatchDiagnostics;
import org.codemucker.jmatch.Matcher;
import org.codemucker.jmatch.ObjectMatcher;

public class AnAnnotation extends ObjectMatcher<Annotation> {

    public static AnAnnotation with() {
        return new AnAnnotation();
    }

    public AnAnnotation() {
        super(Annotation.class);
    }

    public <A extends Annotation> AnAnnotation fullName(final Class<A> annotationClass) {
        fullName(annotationClass.getName());
        return this;
    }

    public AnAnnotation fullName(final String name) {
        fullName(AString.equalTo(name));
        return this;
    }

    public AnAnnotation fullName(final Matcher<String> matcher) {
        addMatcher(new AbstractNotNullMatcher<Annotation>() {
            @Override
            public boolean matchesSafely(Annotation found, MatchDiagnostics diag) {
                return diag.tryMatch(this, found.annotationType().getName(), matcher);
            }

            @Override
            public void describeTo(Description desc) {
                desc.value("fullName", matcher);
            }
        });
        return this;
    }
    
    public AnAnnotation annotationPresent(final Class<? extends Annotation> annotationPresent) {
        addMatcher(new AbstractNotNullMatcher<Annotation>() {
            @Override
            public boolean matchesSafely(Annotation found, MatchDiagnostics diag) {
                return found.annotationType().isAnnotationPresent(annotationPresent);
            }

            @Override
            public void describeTo(Description desc) {
                desc.value("annotationPresetn", annotationPresent.getClass().getName());
            }
        });
        return this;
    }
}
