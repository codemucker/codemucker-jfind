package org.codemucker.jfind;

import org.codemucker.jfind.ClassScanner.Filter;
import org.codemucker.jmatch.Logical;
import org.codemucker.jmatch.Matcher;
import org.codemucker.lang.IBuilder;

public class ClassFilter implements Filter{
	
	private final Matcher<Root> rootMatcher;
	private final Matcher<RootResource> resourceMatcher;
	private final Matcher<Class<?>> classMatcher;
	
	public static Builder where(){
		return with();
	}
	
	public static Builder with(){
		return new Builder();
	}
	
	private ClassFilter(
			Matcher<Root> rootMatcher
			, Matcher<RootResource> resourceMatcher
			, Matcher<Class<?>> classMatcher
			){
		this.rootMatcher = anyIfNull(rootMatcher);
		this.resourceMatcher = anyIfNull(resourceMatcher);
		this.classMatcher = anyIfNull(classMatcher);
	}
	
    private <T> Matcher<T> anyIfNull(Matcher<T> matcher){
		return Logical.anyIfNull(matcher);
	}
	
	@Override
	public boolean isIncludeResource(RootResource resource) {
		return resourceMatcher.matches(resource);
	}
	
	@Override
	public boolean isIncludeRoot(Root root) {
		return rootMatcher.matches(root);
	}

	@Override
	public boolean isIncludeClassResource(ClassResource resource) {
		return true;
	}

	@Override
	public boolean isIncludeClass(Class<?> classToMatch) {
		return classMatcher.matches(classToMatch);
	}
	
	@Override
	public boolean isIncludeArchive(RootResource archiveFile) {
		return resourceMatcher.matches(archiveFile);
	}

	@Override
    public boolean isInclude(Object obj) {
	    return true;
    }
	
	public static class Builder implements IBuilder<ClassFilter>{
	    
		private Matcher<Root> rootMatcher;
		private Matcher<RootResource> resourceMatcher;
		private Matcher<Class<?>> classMatcher;
		
		@Override
        public ClassFilter build(){
			return new ClassFilter(
				rootMatcher
				, resourceMatcher
				, classMatcher
			);
		}
		
		public Builder classMatches(Matcher<Class<?>> classMatcher) {
        	this.classMatcher = classMatcher;
        	return this;
        }

		public Builder rootMatches(Matcher<Root> matcher) {
        	this.rootMatcher = matcher;
        	return this;
        }
	
		public Builder resourceMatches(Matcher<RootResource> resourceMatcher) {
        	this.resourceMatcher = resourceMatcher;
        	return this;
		}
	}

}