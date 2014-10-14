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

import org.codemucker.jfind.JFind.Builder;
import org.codemucker.jfind.JFind.ErrorCallback;
import org.codemucker.jfind.JFind.Filter;
import org.codemucker.jfind.JFind.IgnoredCallback;
import org.codemucker.jfind.JFind.MatchedCallback;
import org.codemucker.jmatch.Matcher;

public class Criteria {
	
	private Roots.Builder rootsBuilder = Roots.with();
	
	private final IncludeExcludeMatcherBuilder<Root> rootMatcher = IncludeExcludeMatcherBuilder.builder();
	private final IncludeExcludeMatcherBuilder<RootResource> resourceMatcher = IncludeExcludeMatcherBuilder.builder();
    private final IncludeExcludeMatcherBuilder<Class<?>> classMatcher = IncludeExcludeMatcherBuilder.builder();

    private IgnoredCallback ignoredCallback;
    private MatchedCallback matchedCallback; 
    private ErrorCallback errorCallback;

    private ClassLoader classLoader;
    
    public static Criteria with(){
        return new Criteria();
    }
    
	public JFind build() {

		Filter filter = JFindFilter.with()
		    .rootMatches(rootMatcher.build())
		    .resourceMatches(resourceMatcher.build())
            .classMatches(classMatcher.build())
			//.classNameMatches(classNames.build())
			//.setResourceNameMatcher(resourceNames.build())
			.build();
		
		Builder builder = JFind.with()
		    .roots(rootsBuilder.build())
		    .filter(filter);
		
		//if any null default will be used
		builder.matchedCallback(matchedCallback);
		builder.ignoredCallback(ignoredCallback);
		builder.errorCallback(errorCallback);
		builder.classLoader(classLoader);
		
	    return builder.build();
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
    
	public Criteria ignoreCallback(IgnoredCallback callback){
		this.ignoredCallback = callback;
		return this;
	}
	
	public Criteria matchCallback(MatchedCallback callback){
	    this.matchedCallback = callback;
	    return this;
	}
	
	public Criteria errorCallback(ErrorCallback callback){
		this.errorCallback = callback;
		return this;
	}

	public Criteria classLoader(ClassLoader classLoader) {
    	this.classLoader = classLoader;
    	return this;
    }

	public Criteria excludeResource(Matcher<RootResource> matcher) {
		resourceMatcher.addExclude(matcher);
		return this;
	}

	public Criteria includeResource(Matcher<RootResource> matcher) {
		resourceMatcher.addInclude(matcher);
		return this;
	}

	public Criteria includeClass(Matcher<Class<?>> matcher) {
		classMatcher.addInclude(matcher);
		return this;
	}

	public Criteria excludeClass(Matcher<Class<?>> matcher) {
		classMatcher.addExclude(matcher);
		return this;
	}

}