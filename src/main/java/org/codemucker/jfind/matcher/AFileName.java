package org.codemucker.jfind.matcher;

import java.util.regex.Pattern;

import org.codemucker.jmatch.AString;
import org.codemucker.jmatch.Logical;
import org.codemucker.jmatch.Matcher;
import org.codemucker.jmatch.ObjectMatcher;

public class AFileName extends ObjectMatcher<String> {
	
	public static AFileName with(){
		return new AFileName();
	}
	
	private AFileName(){
	    super(String.class);
	}
	
    public static Matcher<String> any() {
    	return Logical.any();
    }
    
	public AFileName packageName(String packageName) {
		String regExp = "/" + packageName.replace('.', '/') + "/.*";
		pathRegexMatch(Pattern.compile(regExp));
		return this;
	}

	public AFileName extension(String extension) {
		pathAntPatternMatch("*." + extension);
		return this;
	}

	public AFileName name(Class<?> classToMatch) {
		String path = '/' + classToMatch.getSimpleName() + "\\.java";
		Package pkg = classToMatch.getPackage();
		if (pkg != null) {
			path = '/' + pkg.getName().replace('.', '/') + path;
		}
		pathRegexMatch(Pattern.compile(path));
		return this;
	}
	
	public AFileName nameAntPatternMatch(String antPattern) {
		pathAntPatternMatch("*/" + antPattern);
		return this;
	}

	public AFileName packageName(Class<?> classWithPkg) {
		packageName(classWithPkg.getPackage());
		return this;
	}

	public AFileName packageName(Package pkg) {
		pathAntPatternMatch(pkg.toString().replace('.', '/'));
		return this;
	}
	
	public AFileName pathAntPatternMatch(String antPattern) {
		addMatcher(AString.matchingAntPattern(antPattern));
		return this;
	}

	public AFileName pathRegexMatch(Pattern pattern) {
		addMatcher(AString.matchingRegex(pattern));
		return this;
	}
}
