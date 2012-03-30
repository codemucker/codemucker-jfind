package com.bertvanbrakel.test.finder.matcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;

import com.bertvanbrakel.test.finder.ClassPathResource;

public class ClassMatchers extends LogicalMatchers {

	public static final Matcher<Class<?>> MATCHER_ANONYMOUS = new Matcher<Class<?>>() {
		@Override
		public boolean matches(Class<?> found) {
			return found.isAnonymousClass();
		}
	};
	
	public static final Matcher<Class<?>> MATCHER_ENUM = new Matcher<Class<?>>() {
		@Override
		public boolean matches(Class<?> found) {
			return found.isEnum();
		}
	};
	
	public static final Matcher<Class<?>> MATCHER_INNER_CLASS = new Matcher<Class<?>>() {
		@Override
		public boolean matches(Class<?> found) {
			return found.isMemberClass();
		}
	};

	public static final Matcher<Class<?>> MATCHER_INTERFACE = new Matcher<Class<?>>() {
		@Override
		public boolean matches(Class<?> found) {
			return found.isInterface();
		}
	};
	
	@SuppressWarnings("unchecked")
    public static Matcher<Class<?>> anyClass() {
    	return LogicalMatchers.any();
    }
    
    @SuppressWarnings("unchecked")
    public static Matcher<Class<?>> noClass() {
    	return LogicalMatchers.none();
    }
	
    public static Matcher<Class<?>> withModifier(final int modifier){
    	return new Matcher<Class<?>>(){
			@Override
            public boolean matches(Class<?> found) {
	            return (found.getModifiers() & modifier) != 0;
            }
    	};
    }
	public static Matcher<Class<?>> assignableTo(Class<?>... superclass) {
		return new ClassAssignableMatcher(superclass);
	}
	
	public static Matcher<Class<?>> withAnnotation(Class<? extends Annotation>... annotations){
		return new ContainsAnnotationsMatcher(annotations);
	}
	
	public static Matcher<Class<?>> excludeEnum() {
		return not(MATCHER_ENUM);
	}

	public static Matcher<Class<?>> excludeAnonymous() {
		return not(MATCHER_ANONYMOUS);
	}

	public static Matcher<Class<?>> excludeInner() {
		return not(MATCHER_INNER_CLASS);
	}

	public static Matcher<Class<?>> excludeInterfaces() {
		return not(MATCHER_INTERFACE);
	}

	public static Matcher<Class<?>> includeEnum() {
		return MATCHER_ENUM;
	}

	public static Matcher<Class<?>> includeAnonymous() {
		return MATCHER_ANONYMOUS;
	}

	public static Matcher<Class<?>> includeInner() {
		return MATCHER_INNER_CLASS;
	}

	public static Matcher<Class<?>> includeInterfaces() {
		return MATCHER_INTERFACE;
	}
}
