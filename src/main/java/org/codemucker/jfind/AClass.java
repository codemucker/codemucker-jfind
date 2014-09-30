package org.codemucker.jfind;

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
	
	/**
	 * synonym for with()
	 * @return
	 */
	public static AClass that(){
		return with();
	}
	
	public static AClass with(){
		return new AClass();
	}
	
	private AClass() {
	    super(Class.class);
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
    	addMatcher(new AbstractNotNullMatcher<Class<?>>(){
			@Override
            public boolean matchesSafely(Class<?> found,MatchDiagnostics diag) {
	            return (found.getModifiers() & modifier) != 0;
            }
    	});
    	return this;
    }
    
    /**
     * Expect a class to be a sub class, implementation of, or equal to the given superclass
     * @param superclass
     * @return
     */
	public AClass isASubclassOf(final Class<?>... superclass) {
		addMatcher(new AbstractNotNullMatcher<Class<?>>() {
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
					.add("subclass of", superclass)
					.toString();
			}
		});
		return this;
	}
	/**
	 * Class must be marked with the given annotation
	 * @param annotations
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public AClass annotation(Class<? extends Annotation>... annotations){
		addMatcher(new ContainsAnnotationsMatcher(annotations));
		return this;
	}
	
	public AClass notEnum() {
		addMatcher(Logical.not(MATCHER_ENUM));
		return this;
	}

	public AClass isNotAnonymous() {
		addMatcher(Logical.not(MATCHER_ANONYMOUS));
		return this;
	}

	public AClass isNotInner() {
		addMatcher(Logical.not(MATCHER_INNER_CLASS));
		return this;
	}

	public AClass isNotInterface() {
		addMatcher(Logical.not(MATCHER_INTERFACE));
		return this;
	}

	public AClass isEnum() {
		addMatcher(MATCHER_ENUM);
		return this;
	}

	public AClass isAnonymous() {
		addMatcher(MATCHER_ANONYMOUS);
		return this;
	}

	public AClass isInnerClass() {
		addMatcher(MATCHER_INNER_CLASS);
		return this;
	}

	public AClass isInterface() {
		addMatcher(MATCHER_INTERFACE);
		return this;
	}
	
	private static class ContainsAnnotationsMatcher extends AbstractNotNullMatcher<Class<?>> {
		private final Class<? extends Annotation>[] annotations;

		@SafeVarargs
		public ContainsAnnotationsMatcher(Class<? extends Annotation>... annotations) {
	        this.annotations = annotations;
	    }

		@Override
		@SuppressWarnings("rawtypes")
		public boolean matchesSafely(Class found,MatchDiagnostics diag) {
			for (Class<?> anon : annotations) {
				if (found.getAnnotation(anon) == null) {
					return false;
				}
			}
			return true;
		}
	}
}
