package org.codemucker.jfind.matcher;

import java.lang.annotation.Annotation;

import org.codemucker.jmatch.AbstractNotNullMatcher;
import org.codemucker.jmatch.Logical;
import org.codemucker.jmatch.MatchDiagnostics;
import org.codemucker.jmatch.Matcher;
import org.codemucker.jmatch.ObjectMatcher;

import com.google.common.base.Objects;

public class AClass extends ObjectMatcher<Class<?>> {

	public static final Matcher<Class<?>> MATCHER_ANONYMOUS = new AbstractNotNullMatcher<Class<?>>() {
		@Override
		public boolean matchesSafely(Class<?> found,MatchDiagnostics diag) {
			return found.isAnonymousClass();
		}
	};
	
	public static final Matcher<Class<?>> MATCHER_ENUM = new AbstractNotNullMatcher<Class<?>>() {
		@Override
		public boolean matchesSafely(Class<?> found,MatchDiagnostics diag) {
			return found.isEnum();
		}
	};
	
	public static final Matcher<Class<?>> MATCHER_INNER_CLASS = new AbstractNotNullMatcher<Class<?>>() {
		@Override
		public boolean matchesSafely(Class<?> found,MatchDiagnostics diag) {
			return found.isMemberClass();
		}
	};

	public static final Matcher<Class<?>> MATCHER_INTERFACE = new AbstractNotNullMatcher<Class<?>>() {
		@Override
		public boolean matchesSafely(Class<?> found,MatchDiagnostics diag) {
			return found.isInterface();
		}
	};
	
	public static AClass with(){
		return new AClass();
	}
	
	private AClass() {
	}

	@SafeVarargs
    public static Matcher<Class<?>> any(final Matcher<Class<?>>... matchers) {
    	return Logical.any(matchers);
    }
	
	@SafeVarargs
    public static Matcher<Class<?>> all(final Matcher<Class<?>>... matchers) {
    	return Logical.all(matchers);
    }
	
    public static Matcher<Class<?>> anyClass() {
    	return Logical.any();
    }
    
    public static Matcher<Class<?>> noClass() {
    	return Logical.none();
    }
	
    public AClass modifier(final int modifier){
    	withMatcher(new AbstractNotNullMatcher<Class<?>>(){
			@Override
            public boolean matchesSafely(Class<?> found,MatchDiagnostics diag) {
	            return (found.getModifiers() & modifier) != 0;
            }
    	});
    	return this;
    }
    
	public AClass superclass(final Class<?>... superclass) {
		withMatcher(new AbstractNotNullMatcher<Class<?>>() {
			@Override
			public boolean matchesSafely(Class<?> found,MatchDiagnostics diag) {
				for (Class<?> require : superclass) {
					if (!require.isAssignableFrom(found)) {
						return false;
					}
				}
				return true;
			}
			
			@Override
			public String toString(){
				return Objects.toStringHelper(this)
					.add("superClassesMatching", superclass)
					.toString();
			}
		});
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public AClass annotation(Class<? extends Annotation>... annotations){
		withMatcher(new ContainsAnnotationsMatcher(annotations));
		return this;
	}
	
	public AClass notEnum() {
		withMatcher(Logical.not(MATCHER_ENUM));
		return this;
	}

	public AClass isNotAnonymous() {
		withMatcher(Logical.not(MATCHER_ANONYMOUS));
		return this;
	}

	public AClass isNotInner() {
		withMatcher(Logical.not(MATCHER_INNER_CLASS));
		return this;
	}

	public AClass isNotInterface() {
		withMatcher(Logical.not(MATCHER_INTERFACE));
		return this;
	}

	public AClass isEnum() {
		withMatcher(MATCHER_ENUM);
		return this;
	}

	public AClass isAnonymous() {
		withMatcher(MATCHER_ANONYMOUS);
		return this;
	}

	public AClass isInnerClass() {
		withMatcher(MATCHER_INNER_CLASS);
		return this;
	}

	public AClass isInterface() {
		withMatcher(MATCHER_INTERFACE);
		return this;
	}
}
