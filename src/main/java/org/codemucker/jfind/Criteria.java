/*
 * Copyright 2011 Bert van Brakel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codemucker.jfind;

import java.lang.annotation.Annotation;
import java.util.regex.Pattern;

import org.codemucker.jfind.ClassFinder.Builder;
import org.codemucker.jfind.ClassFinder.FinderErrorCallback;
import org.codemucker.jfind.ClassFinder.FinderFilter;
import org.codemucker.jfind.ClassFinder.FinderIgnoredCallback;
import org.codemucker.jfind.ClassFinder.FinderMatchedCallback;
import org.codemucker.jmatch.Matcher;


public class Criteria {
	
	private Roots.Builder rootsBuilder = Roots.with();
	
	private final IncludeExcludeMatcherBuilder<RootResource> resources = IncludeExcludeMatcherBuilder.builder();
	private final IncludeExcludeMatcherBuilder<Class<?>> classes = IncludeExcludeMatcherBuilder.builder();
	private final IncludeExcludeMatcherBuilder<String> classNames = IncludeExcludeMatcherBuilder.builder();
	private final IncludeExcludeMatcherBuilder<String> resourceNames = IncludeExcludeMatcherBuilder.builder();
	
	private ClassFinder.Builder builder = ClassFinder.withBuilder();

	public ClassFinder build() {
		Builder copy = builder.copyOf();
		
		FinderFilter filter = ClassFinderFilter.newBuilder()
			.setClassMatcher(classes.build())
			.setResourceMatcher(resources.build())
			.setClassNameMatcher(classNames.build())
			.setResourceNameMatcher(resourceNames.build())
			.build();
		copy.setFilter(filter);
		copy.setSearchClassPaths(rootsBuilder.build());
		
		
		return copy.build();
	}
	
	//TODO:reinstate
	public Criteria setConsoleLoggingCallback(){
	//	ConsoleLoggingCallBack callback = new ConsoleLoggingCallBack();
	//	setIgnoreCallback(callback);
	//	setMatchCallback(callback);
		return this;
	}

	public Criteria root(Root root){
		rootsBuilder.root(root);
		return this;
	}
	
	public Criteria roots(Iterable<Root> roots){
		rootsBuilder = Roots.with(roots);
		return this;
	}
	
    public Criteria ignoreCallbackLogging() {
        ignoreCallback(new LoggingIgnoredCallback());
        return this;
    }
    
	public Criteria ignoreCallback(FinderIgnoredCallback callback){
		builder.setIgnoredCallback(callback);
		return this;
	}
	
	public Criteria matchCallback(FinderMatchedCallback callback){
		builder.setMatchedCallback(callback);
		return this;
	}
	
	public Criteria errorCallback(FinderErrorCallback callback){
		builder.setErrorCallback(callback);
		return this;
	}

	public Criteria classLoader(ClassLoader classLoader) {
    	builder.setClassLoader(classLoader);
    	return this;
    }

	public Criteria excludeFileName(String path) {
		excludeResource(AResource.with().pathMatchingAntPattern(path));
		return this;
	}
	
	public Criteria excludeFileName(Pattern pattern) {
		excludeResource(AResource.with().pathMatchingRegex(pattern));
		return this;
	}

	public Criteria excludeResource(Matcher<RootResource> matcher) {
		resources.addExclude(matcher);
		return this;
	}

	public Criteria includeFileName(String pattern) {
		includeResource(AResource.with().pathMatchingAntPattern(pattern));
		return this;
	}

	public Criteria includeFileName(Pattern pattern) {
		includeResource(AResource.with().pathMatchingRegex(pattern));
		return this;
	}
	
	public Criteria includeResource(Matcher<RootResource> matcher) {
		resources.addInclude(matcher);
		return this;
	}
	
	public Criteria assignableTo(Class<?>... superclass) {
		includeClass(AClass.with().isASubclassOf(superclass));
		return this;
	}
	
	public <T extends Annotation> Criteria withAnnotation(Class<T>... annotations){
		includeClass(AClass.with().annotation(annotations));
		return this;
	}
	
	public Criteria includeClass(Matcher<Class<?>> matcher) {
		classes.addInclude(matcher);
		return this;
	}
	
	public Criteria excludeEnum() {
		excludeClassMatching(AClass.with().isEnum());
		return this;
	}

	public Criteria excludeAnonymous() {
		excludeClassMatching(AClass.with().isAnonymous());
		return this;
	}

	public Criteria excludeInner() {
		excludeClassMatching(AClass.with().isInnerClass());
		return this;
	}

	public Criteria excludeInterfaces() {
		excludeClassMatching(AClass.with().isInterface());
		return this;
	}

	public Criteria excludeClassMatching(Matcher<Class<?>> matcher) {
		classes.addExclude(matcher);
		return this;
	}

}