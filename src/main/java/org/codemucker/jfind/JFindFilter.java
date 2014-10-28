package org.codemucker.jfind;

import org.codemucker.jfind.JFind.Filter;
import org.codemucker.jmatch.Logical;
import org.codemucker.jmatch.Matcher;
import org.codemucker.lang.IBuilder;

public class JFindFilter implements Filter{
	
	private final Matcher<Root> rootMatcher;
	private final Matcher<RootResource> resourceMatcher;
	private final Matcher<String> packageNameMatcher;
	private final Matcher<String> classNameMatcher;
	private final Matcher<Class<?>> classMatcher;
	
	public static Builder with(){
		return new Builder();
	}
	
	private JFindFilter(
			Matcher<Root> classPathMatcher
			, Matcher<RootResource> resourceMatcher
			, Matcher<String> resourceNameMatcher
			, Matcher<String> packageNameMatcher
			, Matcher<String> classNameMatcher
			, Matcher<Class<?>> classMatcher
			){
		this.rootMatcher = anyIfNull(classPathMatcher);
		this.resourceMatcher = anyIfNull(resourceMatcher);
		this.classNameMatcher = anyIfNull(classNameMatcher);
		this.packageNameMatcher = anyIfNull(packageNameMatcher);
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
	public boolean isIncludeDir(RootResource resource) {
		return true;//fileMatcher.matches(resource);
	}
	
	@Override
	public boolean isIncludeRoot(Root root) {
		return rootMatcher.matches(root);
	}
	
	@Override
	public boolean isIncludeClassname(RootResource resource,String className) {
	    return packageNameMatcher.matches(extractPackageName(className)) && classNameMatcher.matches(className);
	}
	
	private static String extractPackageName(String className){
	    int lastdot = className.lastIndexOf('.');
	    if(lastdot !=-1){
	        return className.substring(0,lastdot);
	    }
	    return "";
	}
	
	@Override
	public boolean isIncludeClass(RootResource resource,Class<?> classToMatch) {
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
	
	public static class Builder implements IBuilder<JFindFilter>{
	    
		private Matcher<Root> rootMatcher;
		private Matcher<RootResource> resourceMatcher;
		private Matcher<String> packageNameMatcher;
		private Matcher<String> classNameMatcher;
		private Matcher<Class<?>> classMatcher;
		
		public JFindFilter build(){
			return new JFindFilter(
				rootMatcher
				, resourceMatcher
				, null//resourceNameMatcher
				, packageNameMatcher
				, classNameMatcher
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
		
		public Builder packageNameMatches(Matcher<String> packageNameMatcher) {
            this.packageNameMatcher = packageNameMatcher;
            return this;
        }
		
		public Builder classNameMatches(Matcher<String> classNameMatcher) {
		    this.classNameMatcher = classNameMatcher;
		    return this;
		}
		
		/*
		public Builder setResourceNameMatcher(Matcher<String> fileNameMatcher) {
        	this.resourceNameMatcher = fileNameMatcher;
        	return this;
		}
*/
		public Builder resourceMatches(Matcher<RootResource> resourceMatcher) {
        	this.resourceMatcher = resourceMatcher;
        	return this;
		}
	}

}