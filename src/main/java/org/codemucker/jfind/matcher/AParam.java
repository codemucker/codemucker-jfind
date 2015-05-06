package org.codemucker.jfind.matcher;

import java.lang.annotation.Annotation;

import org.codemucker.jfind.matcher.AMethod.Param;
import org.codemucker.jmatch.AbstractMatcher;
import org.codemucker.jmatch.AbstractNotNullMatcher;
import org.codemucker.jmatch.AnInt;
import org.codemucker.jmatch.Description;
import org.codemucker.jmatch.MatchDiagnostics;
import org.codemucker.jmatch.Matcher;
import org.codemucker.jmatch.PropertyMatcher;

public class AParam extends PropertyMatcher<Param>{

	public static AParam with(){
		return new AParam();
	}
	
	public AParam() {
		super(Param.class);
	}

	public AParam type(final Matcher<Class<?>> matcher){
		addMatcher(new AbstractMatcher<Param>() {

			@Override
			protected boolean matchesSafely(Param actual, MatchDiagnostics diag) {
				return diag.tryMatch(this, actual.getParamType(), matcher);
			}

			@Override
			public void describeTo(Description desc) {
				desc.value("type", matcher);
			}
		});
		return this;
	}

    public AParam annotation(final Class<? extends Annotation> annotationClass) {
    	annotation(AnAnnotation.with().fullName(annotationClass));
    	return this;
    }
    
    public AParam annotation(final Matcher<Annotation> matcher) {
        addMatcher(new AbstractNotNullMatcher<Param>() {

            @Override
            protected boolean matchesSafely(Param actual, MatchDiagnostics diag) {
        	   for (Annotation a : actual.getParamAnnotations()) {
                    if (matcher.matches(a)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void describeTo(Description desc) {
                desc.value("with annotation", matcher);
            }
        });
        return this;
    }
    
    public AParam position(final int position){
    	position(AnInt.equalTo(position));
    	return this;
    }
    
	public AParam position(final Matcher<Integer> matcher){
		addMatcher(new AbstractMatcher<Param>() {

			@Override
			protected boolean matchesSafely(Param actual, MatchDiagnostics diag) {
				return diag.tryMatch(this, actual.getParamPosition(), matcher);
			}

			@Override
			public void describeTo(Description desc) {
				desc.value("position", matcher);
			}
		});
		return this;
	}
}
