package org.codemucker.jfind;

import java.lang.reflect.Method;

import org.codemucker.jmatch.AString;
import org.codemucker.jmatch.AbstractMatcher;
import org.codemucker.jmatch.AnInt;
import org.codemucker.jmatch.Description;
import org.codemucker.jmatch.Logical;
import org.codemucker.jmatch.MatchDiagnostics;
import org.codemucker.jmatch.Matcher;
import org.codemucker.jmatch.ObjectMatcher;
import org.codemucker.jmatch.PropertyMatcher;

public class AMethod extends PropertyMatcher<Method>{

	public AMethod() {
		super(Method.class);
	}

	public static AMethod that(){
		return with();
	}
	
	public static AMethod with(){
		return new AMethod();
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
		addMatchProperty("name", String.class, matcher);
		return this;
	}
	
	public AMethod isVoid(final boolean b){
		addMatcher(new AbstractMatcher<Method>() {
			@Override
			protected boolean matchesSafely(Method found, MatchDiagnostics diag) {
				boolean isVoid = found.getReturnType() == null || found.getReturnType() == Void.class;
				return isVoid == b;
			}
			
			@Override
			public void describeTo(Description desc) {
				desc.text("isVoid", b);
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
				return diag.TryMatch(found.getParameterTypes().length, matcher);
			}
			
			@Override
			public void describeTo(Description desc) {
				desc.text("numArgs", matcher);
			}
		});
		return this;
	}
	
	
}
