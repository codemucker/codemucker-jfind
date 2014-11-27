package org.codemucker.jfind.matcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.codemucker.jfind.ReflectedMethod;
import org.codemucker.jmatch.AString;
import org.codemucker.jmatch.AbstractMatcher;
import org.codemucker.jmatch.AbstractNotNullMatcher;
import org.codemucker.jmatch.AnInt;
import org.codemucker.jmatch.Description;
import org.codemucker.jmatch.Logical;
import org.codemucker.jmatch.MatchDiagnostics;
import org.codemucker.jmatch.Matcher;

public class AMethod extends AbstractModiferMatcher<AMethod,Method>{

	public AMethod() {
		super(Method.class);
	}

	public static AMethod that(){
		return with();
	}
	
	public static AMethod with(){
		return new AMethod();
	}
	
    @Override
    protected int getModifier(Method instance) {
        return instance.getModifiers();
    }
	
	public AMethod isGetter(){
		name(Logical.any(AString.matchingAntPattern("get*"),AString.matchingAntPattern("is*")));
		numArgs(0);
		return this;
	}
	
	public AMethod isSetter(){
		name("set*");
		numArgs(AnInt.greaterOrEqualTo(1));
		return this;
	}
	
	public AMethod name(String nameAntPattern){
		name(AString.matchingAntPattern(nameAntPattern));
		return this;
	}
	
	public AMethod name(Matcher<String> matcher){
		matchProperty("name", String.class, matcher);
		return this;
	}
	
	public AMethod isVoidReturn(){
	    isVoidReturn(true);
	    return this;
	}
	
	public AMethod isNotVoidReturn(){
        isVoidReturn(false);
        return this;
    }
    
	public AMethod isVoidReturn(final boolean b){
		addMatcher(new AbstractMatcher<Method>() {
			@Override
			protected boolean matchesSafely(Method found, MatchDiagnostics diag) {
				boolean isVoid = found.getReturnType() == null || found.getReturnType() == Void.class;
				return isVoid == b;
			}
			
			@Override
			public void describeTo(Description desc) {
				desc.text("is" + (b?"":" not")+ " void");
			}
		});
		return this;
	}
	
	public AMethod annotation(Class<? extends Annotation> annotation){
        annotation(AnAnnotation.with().fullName(annotation));
        return this;
    }
    
    public AMethod annotation(final Matcher<Annotation> matcher) {
        addMatcher(new AbstractNotNullMatcher<Method>() {

            @Override
            protected boolean matchesSafely(Method found, MatchDiagnostics diag) {
                return found.getAnnotations().length > 0 && ReflectedMethod.from(found).hasAnnotation(matcher);
            }

            @Override
            public void describeTo(Description desc) {
                desc.value("with annotation", matcher);
            }
        });
        return this;
    }

	
	public AMethod numArgs(int val){
		numArgs(AnInt.equalTo(val));
		return this;
	}
	
	public AMethod numArgs(final Matcher<Integer> matcher){
		addMatcher(new AbstractMatcher<Method>() {
			@Override
			protected boolean matchesSafely(Method found, MatchDiagnostics diag) {
				return diag.tryMatch(this,found.getParameterTypes().length, matcher);
			}
			
			@Override
			public void describeTo(Description desc) {
				desc.text("numArgs " + matcher);
			}
		});
		return this;
	}

	public AMethod argType(final Matcher<Class<?>> matcher){
	    arg(new AbstractMatcher<AMethod.Param>(){

            @Override
            protected boolean matchesSafely(Param actual, MatchDiagnostics diag) {
                return diag.tryMatch(this, actual.getParamType(), matcher);
            }
            
            @Override
            public void describeTo(Description desc) {
                desc.value("type",matcher);
            }
	    });
	    return this;
	}
	
	public AMethod arg(final Matcher<Param> matcher){
        addMatcher(new AbstractMatcher<Method>() {
            @Override
            protected boolean matchesSafely(Method found, MatchDiagnostics diag) {
                Class<?>[] types = found.getParameterTypes();
                if(types.length == 0){
                    return false;
                }
                Annotation[][] annotations = found.getParameterAnnotations();
                
                //reuse
                Param p = new Param();
                for(int i = 0; i < types.length;i++){
                    p.paramPosition = i;
                    p.paramType = types[i];
                    p.paramAnnotations = annotations[i];
                    if(matcher.matches(p)){
                        return true;
                    }
                }
                return false;
            }
            
            @Override
            public void describeTo(Description desc) {
                desc.value("method arg", matcher);
            }
        });
        return this;
    }
	

    /**
     * Wraps up a number of details about a method arg. Not to be held on to after a match as the instance is reused
     */
    public static class Param {
        private Class<?> paramType;
        private Annotation[] paramAnnotations;
        private int paramPosition;
        
        public Class<?> getParamType() {
            return paramType;
        }
        
        public Annotation[] getParamAnnotations() {
            return paramAnnotations;
        }

        public int getParamPosition() {
            return paramPosition;
        }        
    }
    
}
