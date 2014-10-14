package org.codemucker.jfind.matcher;

import java.lang.annotation.Annotation;

import org.codemucker.jmatch.AbstractNotNullMatcher;
import org.codemucker.jmatch.Description;
import org.codemucker.jmatch.Logical;
import org.codemucker.jmatch.MatchDiagnostics;
import org.codemucker.jmatch.Matcher;

public class AClass extends AbstractModiferMatcher<AClass,Class<?>> {

	public static final Matcher<Class<?>> MATCHER_ANONYMOUS = new AbstractNotNullMatcher<Class<?>>() {
		@Override
		public boolean matchesSafely(Class<?> found,MatchDiagnostics diag) {
			return found.isAnonymousClass();
		}
		@Override
        public void describeTo(Description desc) {
            desc.text("is anonymous");
        }
	};
	
	public static final Matcher<Class<?>> MATCHER_ENUM = new AbstractNotNullMatcher<Class<?>>() {
		@Override
		public boolean matchesSafely(Class<?> found,MatchDiagnostics diag) {
			return found.isEnum();
		}
		@Override
		public void describeTo(Description desc) {
		    desc.text("is enum");
		}
	};
	
	public static final Matcher<Class<?>> MATCHER_INNER_CLASS = new AbstractNotNullMatcher<Class<?>>() {
		@Override
		public boolean matchesSafely(Class<?> found,MatchDiagnostics diag) {
			return found.isMemberClass();
		}
		@Override
        public void describeTo(Description desc) {
            desc.text("is inner class");
        }
	};

	public static final Matcher<Class<?>> MATCHER_INTERFACE = new AbstractNotNullMatcher<Class<?>>() {
		@Override
		public boolean matchesSafely(Class<?> found,MatchDiagnostics diag) {
			return found.isInterface();
		}
		@Override
        public void describeTo(Description desc) {
            desc.text("is interface");
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

    @Override
    protected int getModifier(Class<?> instance) {
        return instance.getModifiers();
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
	        public void describeTo(Description desc) {
	            desc.value("is subclass of", superclass);
	        }
		});
		return this;
	}
	
    public AClass annotation(Class<? extends Annotation> annotation){
        addMatcher(new ContainsAnnotationsMatcher(annotation));
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
	
	public AClass isPublicConcreteClass() {
	    isNotAnonymous();
	    isNotInterface();
	    isNotInner();
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
		
		@Override
        public void describeTo(Description desc) {
            desc.value("contains all annotations", annotations);
        }
	}
}
