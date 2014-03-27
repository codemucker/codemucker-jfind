package org.codemucker.jfind;

import java.util.regex.Pattern;

import org.codemucker.jmatch.AString;
import org.codemucker.jmatch.AbstractMatcher;
import org.codemucker.jmatch.Description;
import org.codemucker.jmatch.Logical;
import org.codemucker.jmatch.MatchDiagnostics;
import org.codemucker.jmatch.Matcher;
import org.codemucker.jmatch.PropertyMatcher;

import com.google.common.base.Preconditions;

public class AResource extends PropertyMatcher<RootResource> {
	
	public static AResource with(){
		return new AResource();
	}
	
	private AResource(){
		super(RootResource.class);
	}
	
    public static Matcher<RootResource> any() {
    	return Logical.any();
    }
    
    public static Matcher<RootResource> none() {
    	return Logical.none();
    }
    
	public AResource packageName(String packageName) {
		String regExp = "/" + packageName.replace('.', '/') + "/.*";
		pathRegexMatch(Pattern.compile(regExp));
		return this;
	}

	public AResource extension(String extension) {
		pathAntPatternMatch("*." + extension);
		return this;
	}

	public AResource fullName(Class<?> classToMatch) {
		String path = '/' + classToMatch.getSimpleName() + "\\.java";
		Package pkg = classToMatch.getPackage();
		if (pkg != null) {
			path = '/' + pkg.getName().replace('.', '/') + path;
		}
		pathRegexMatch(Pattern.compile(path));
		return this;
	}
	
	public AResource nameAntPatternMatch(String antPattern) {
		pathAntPatternMatch("*/" + antPattern);
		return this;
	}

	public AResource packageName(Class<?> classWithPkg) {
		packageName(classWithPkg.getPackage());
		return this;
	}

	public AResource packageName(Package pkg) {
		pathAntPatternMatch(pkg.toString().replace('.', '/'));
		return this;
	}

	public AResource pathAntPatternMatch(String antPattern) {
		path(AString.withAntPattern(antPattern));
		return this;
	}

	public AResource pathRegexMatch(Pattern pattern) {
		path(AString.withPattern(pattern));
		return this;
	}
	
	public AResource path(Matcher<String> pathMatcher) {
		addMatcher(new ResourcePathMatcher(pathMatcher));
		return this;
	}
	
	private static class ResourcePathMatcher extends AbstractMatcher<RootResource>{
		private final Matcher<String> pathMatcher;

		ResourcePathMatcher(Matcher<String> pathMatcher){
			this.pathMatcher = Preconditions.checkNotNull(pathMatcher,"null path matcher");
		}

		@Override
		public boolean matchesSafely(RootResource actual,MatchDiagnostics diag) {
			return actual != null && diag.TryMatch(actual.getRelPath(), pathMatcher);
		}
		
		@Override
		public void describeTo(Description desc) {
			super.describeTo(desc);
			desc.text("not null resouce");
			desc.value("relPath", pathMatcher);
		}
		
	}
}
