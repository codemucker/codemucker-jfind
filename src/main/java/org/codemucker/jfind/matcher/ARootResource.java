package org.codemucker.jfind.matcher;

import java.io.IOException;
import java.util.regex.Pattern;

import org.codemucker.jfind.RootResource;
import org.codemucker.jmatch.AString;
import org.codemucker.jmatch.AbstractMatcher;
import org.codemucker.jmatch.Description;
import org.codemucker.jmatch.Logical;
import org.codemucker.jmatch.MatchDiagnostics;
import org.codemucker.jmatch.Matcher;
import org.codemucker.jmatch.PropertyMatcher;
import org.codemucker.lang.PathUtil;

import com.google.common.base.Preconditions;

public class ARootResource extends PropertyMatcher<RootResource> {
	
	public static ARootResource with(){
		return new ARootResource();
	}
	
	public ARootResource(){
		super(RootResource.class);
	}
	
    public static Matcher<RootResource> any() {
    	return Logical.any();
    }
    
    @SafeVarargs
    public static Matcher<RootResource> any(Matcher<RootResource>... matchers) {
        return Logical.any(matchers);
    }
    
    public static Matcher<RootResource> none() {
    	return Logical.none();
    }
    
    public static Matcher<RootResource> not(Matcher<RootResource> matcher) {
        return Logical.not(matcher);
    }
    
    public ARootResource packageName(Class<?> classWithPkg) {
        packageName(classWithPkg.getPackage());
        return this;
    }

    public ARootResource packageName(Package pkg) {
        packageName(pkg.getName());
        return this;
    }

    public ARootResource packageName(String packageName) {
        packageName(AString.equalTo(packageName));
        return this;
    }
    
	public ARootResource packageName(Matcher<String> packageNameMatcher) {
		addMatcher(new ResourcePackageNameMatcher(packageNameMatcher));
		return this;
	}

	public ARootResource className(Class<?> classToMatch) {
        className(AString.equalTo(classToMatch.getName()));
        return this;
    }
    
    public ARootResource className(Matcher<String> classNameMatcher) {
        addMatcher(new ResourceClassNameMatcher(classNameMatcher));
        return this;
    }
    
	public ARootResource extension(String extension) {
		pathMatchingAntPattern("**." + extension);
		return this;
	}

	public ARootResource nameMatchingAntPattern(String antPattern) {
		pathMatchingAntPattern("**/" + antPattern);
		return this;
	}

	public ARootResource pathMatchingAntPattern(String antPattern) {
		path(AString.matchingAntFilePathPattern(antPattern));
		return this;
	}

	public ARootResource pathMatchingRegex(Pattern pattern) {
		path(AString.matchingRegex(pattern));
		return this;
	}
	
	public ARootResource pathEndsWith(String val){
		path(AString.endingWith(val));
		return this;
	}
	
	public ARootResource path(String path) {
		path(AString.equalTo(path));
		return this;
	}
	
	public ARootResource path(Matcher<String> pathMatcher) {
		addMatcher(new ResourcePathMatcher(pathMatcher));
		return this;
	}
	
    public ARootResource stringContent(Matcher<String> contentMatcher) {
        stringContent(contentMatcher, ResourceContentMatcher.DEFAULT_ENCODING);
        return this;
    }

    public ARootResource stringContent(Matcher<String> contentMatcher, String contentEncoding) {
        addMatcher(new ResourceContentMatcher(contentEncoding, contentMatcher));
        return this;
    }
	
	/**
	 * Use a named class so in debug mode the diagnostics debug messages print out a better matcher type message
	 */
	private static class ResourcePathMatcher extends AbstractMatcher<RootResource>{
		private final Matcher<String> pathMatcher;

		ResourcePathMatcher(Matcher<String> pathMatcher){
			this.pathMatcher = Preconditions.checkNotNull(pathMatcher,"null path matcher");
		}

		@Override
		public boolean matchesSafely(RootResource actual,MatchDiagnostics diag) {
			return actual != null && diag.tryMatch(this,actual.getRelPath(), pathMatcher);
		}
		
		@Override
		public void describeTo(Description desc) {
			desc.value("relative path", pathMatcher);
		}
	}

	   /**
     * Use a named class so in debug mode the diagnostics debug messages print out a better matcher type message
     */
    private static class ResourcePackageNameMatcher extends AbstractMatcher<RootResource>{
        private final Matcher<String> pkgMatcher;

        ResourcePackageNameMatcher(Matcher<String> pkgMatcher){
            this.pkgMatcher = Preconditions.checkNotNull(pkgMatcher,"null package name matcher");
        }

        @Override
        public boolean matchesSafely(RootResource actual,MatchDiagnostics diag) {
            String pkg = PathUtil.filePathToPackagePathOrNull(actual.getRelPath());
            return actual != null && diag.tryMatch(this,pkg, pkgMatcher);
        }
        
        @Override
        public void describeTo(Description desc) {
            desc.value("package name", pkgMatcher);
        }
    }

    
	/**
     * Use a named class so in debug mode the diagnostics debug messages print out a better matcher type message
     */
    private static class ResourceClassNameMatcher extends AbstractMatcher<RootResource>{
        private final Matcher<String> classNameMatcher;

        ResourceClassNameMatcher(Matcher<String> classNameMatcher){
            this.classNameMatcher = Preconditions.checkNotNull(classNameMatcher,"null class name matcher");
        }

        @Override
        public boolean matchesSafely(RootResource actual,MatchDiagnostics diag) {
            String fullClassName = PathUtil.filePathToClassNameOrNull(actual.getRelPath());
            return fullClassName != null && diag.tryMatch(this,fullClassName, classNameMatcher);
        }
        
        @Override
        public void describeTo(Description desc) {
            desc.value("class name", classNameMatcher);
        }
    }

    
    /**
     * Use a named class so in debug mode the diagnostics debug messages print out a better matcher type message
     */
    private static class ResourceContentMatcher extends AbstractMatcher<RootResource>{
        public static final String DEFAULT_ENCODING = "utf8";
        private final Matcher<String> contentMatcher;
        private final String contentEncoding;
        

        ResourceContentMatcher(String contentEncoding,Matcher<String> contentMatcher){
            this.contentEncoding = contentEncoding==null?DEFAULT_ENCODING:contentEncoding;
            this.contentMatcher = Preconditions.checkNotNull(contentMatcher,"null content matcher");
        }

        @Override
        public boolean matchesSafely(RootResource actual,MatchDiagnostics diag) {
            try {
                return actual != null && actual.exists() && diag.tryMatch(this,actual.readAsString(contentEncoding), contentMatcher);
            } catch (IOException e) {
                diag.mismatched("couldn't read content");
            }
            return false;
        }
        
        @Override
        public void describeTo(Description desc) {
            desc.value("" + contentEncoding + " content", contentMatcher);
        }
    }
}
