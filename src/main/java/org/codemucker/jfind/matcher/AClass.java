package org.codemucker.jfind.matcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import org.codemucker.jfind.ReflectedClass;
import org.codemucker.jmatch.AString;
import org.codemucker.jmatch.AbstractNotNullMatcher;
import org.codemucker.jmatch.Description;
import org.codemucker.jmatch.Logical;
import org.codemucker.jmatch.MatchDiagnostics;
import org.codemucker.jmatch.Matcher;
import org.codemucker.jmatch.expression.AbstractMatchBuilderCallback;
import org.codemucker.jmatch.expression.ExpressionParser;

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
     * Converts a logical expression using {@link ExpressionParser}} into a matcher. The names in the expression are converted into no arg 
     * method calls on this matcher as in X,isX ==&gt; isX()  (case insensitive) 
     * 
     *  <p>
     * E.g.
     *  <ul>
     *  <li> 
     *  <li>Abstract==&gt; isAbstract()
     *  <li>IsAstract ==&gt; isAbstract()
     *  <li>Anonymous,isAnonymous==&gt; isAnonymous()
     *  
     *  </ul>
     *  </p>
     * @return
     */
    public AClass expression(String expression){
    	if(!isBlank(expression)){
	    	Matcher<Class<?>> matcher = ExpressionParser.parse(expression, new ClassMatchBuilderCallback());
	    	if( matcher instanceof AClass){ //make the matching are bit faster by directly running the matchers directly
	    		for(Matcher<Class<?>> m:((AClass)matcher).getMatchers()){
	    			addMatcher(m);
	    		}
	    	} else {
	    		addMatcher(matcher);
	    	}
    	}
    	return this;
    }
    
    private boolean isBlank(String s){
    	return s==null || s.trim().length() == 0;
    }
    
    public AClass fullName(String fullName){
        fullName(AString.matchingAntPattern(fullName));
        return this;
    }
    
    public AClass fullName(Matcher<String> matcher){
        matchProperty("name", String.class, matcher);
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
	        public void describeTo(Description desc) {
	            desc.value("is subclass of", superclass);
	        }
		});
		return this;
	}
	
    public AClass annotation(Class<? extends Annotation> annotation){
        annotation(AnAnnotation.with().fullName(annotation));
        return this;
    }
	
    public AClass annotation(final Matcher<Annotation> matcher) {
        addMatcher(new AbstractNotNullMatcher<Class<?>>() {

            @Override
            protected boolean matchesSafely(Class<?> actual, MatchDiagnostics diag) {
                return actual.getAnnotations().length > 0 && ReflectedClass.from(actual).hasAnnotation(matcher);
            }

            @Override
            public void describeTo(Description desc) {
                desc.value("with annotation", matcher);
            }
        });
        return this;
    }
    
    public AClass field(final Matcher<Field> matcher) {
        addMatcher(new AbstractNotNullMatcher<Class<?>>() {

            @Override
            protected boolean matchesSafely(Class<?> actual, MatchDiagnostics diag) {                
                return ReflectedClass.from(actual).hasFieldsMatching(matcher);
            }

            @Override
            public void describeTo(Description desc) {
                desc.value("with field", matcher);
            }
        });
        return this;
    }
    
    public AClass method(final Matcher<Method> matcher) {
        addMatcher(new AbstractNotNullMatcher<Class<?>>() {

            @Override
            protected boolean matchesSafely(Class<?> actual, MatchDiagnostics diag) {                
                return ReflectedClass.from(actual).hasMethodMatching(matcher);
            }

            @Override
            public void describeTo(Description desc) {
                desc.value("with method", matcher);
            }
        });
        return this;
    }
    
	public AClass isPublicConcreteClass() {
	    isNotAnonymous();
	    isNotInterface();
	    isNotInnerClass();
	    return this;
    }
	
	public AClass isNotEnum() {
		addMatcher(Logical.not(MATCHER_ENUM));
		return this;
	}

	public AClass isNotAnonymous() {
		addMatcher(Logical.not(MATCHER_ANONYMOUS));
		return this;
	}

	public AClass isNotInnerClass() {
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

    private static class ClassMatchBuilderCallback extends AbstractMatchBuilderCallback<Class<?>>{

    	private static final Object[] NO_ARGS = new Object[]{};
    	
    	private static Map<String, Method> methodMap = new TreeMap<>();
    	
    	private static Matcher<Method> methodMatcher  = AMethod.that().isPublic().isNotAbstract().numArgs(0).isNotVoidReturn().name("is*");
    	
    	static {
    		for(Method m : AClass.class.getDeclaredMethods()){
    			if(methodMatcher.matches(m)){
    				methodMap.put(m.getName().toLowerCase(),m);
    				if(m.getName().startsWith("is")){
    					methodMap.put(m.getName().substring(2).toLowerCase(),m);
    				}
    			}
    		}
    	}

		@Override
		protected Matcher<Class<?>> newMatcher(String expression) {
			// TODO Auto-generated method stub
			AClass matcher = AClass.with();
			String key = expression.trim().toLowerCase();
			Method m = methodMap.get(key);
			if( m==null){
				throw new NoSuchElementException("Could not find method '" + expression.trim() + " on " + AClass.class.getName() + ",options are " + Arrays.toString(methodMap.keySet().toArray()) + "");
			}
			try {
				m.invoke(matcher, NO_ARGS);
			} catch (IllegalAccessException | IllegalArgumentException e) {
				//should never be thrown
				throw new ExpressionParser.ParseException("Error calling " + AClass.class.getName() + "." + m.getName() + "() from expression '" + expression + "'",e);
			}catch (InvocationTargetException e) {
				//should never be thrown
				throw new ExpressionParser.ParseException("Error calling " + AClass.class.getName() + "." + m.getName() + "() from expression '" + expression + "'",e.getTargetException());
			}
			return matcher;
		}
    }
}
