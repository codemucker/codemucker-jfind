package org.codemucker.jfind.matcher;

import java.util.regex.Pattern;

import org.codemucker.jfind.RootResource;
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
    
	public AResource pkg(String packageName) {
		String regExp = "/" + packageName.replace('.', '/') + "/.*";
		path(Pattern.compile(regExp));
		return this;
	}

	public AResource extension(String extension) {
		antPath("*." + extension);
		return this;
	}

	public AResource name(Class<?> classToMatch) {
		String path = '/' + classToMatch.getSimpleName() + "\\.java";
		Package pkg = classToMatch.getPackage();
		if (pkg != null) {
			path = '/' + pkg.getName().replace('.', '/') + path;
		}
		path(Pattern.compile(path));
		return this;
	}
	
	public AResource name(String antPattern) {
		antPath("*/" + antPattern);
		return this;
	}

	public AResource pkg(Class<?> classWithPkg) {
		pkg(classWithPkg.getPackage());
		return this;
	}

	public AResource pkg(Package pkg) {
		antPath(pkg.toString().replace('.', '/'));
		return this;
	}

	public AResource antPath(String antPattern) {
		path(AString.withAntPattern(antPattern));
		return this;
	}

	public AResource path(Pattern pattern) {
		path(AString.withPattern(pattern));
		return this;
	}
	
	public AResource path(Matcher<String> pathMatcher) {
		withMatcher(new ResourcePathMatcher(pathMatcher));
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
