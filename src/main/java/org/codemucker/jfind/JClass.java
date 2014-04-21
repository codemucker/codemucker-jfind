package org.codemucker.jfind;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.codemucker.jmatch.Matcher;

public class JClass {

	private final Class<?> theClass;
	
	public JClass(Class<?> theClass){
		this.theClass = theClass;
	}
	
	public FindResult<Method> findMethodsMatching(Matcher<Method> matcher){
		List<Method> found = new ArrayList<>();
		for(Method m : theClass.getMethods()){
			if(matcher.matches(m)){
				found.add(m);
			}
		}
		return DefaultFindResult.from(found);
	}
}
