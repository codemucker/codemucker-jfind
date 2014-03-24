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
	}
	
    public static Matcher<String> any() {
    	return Logical.any();
    }
    
	public AFileName pkg(String packageName) {
		String regExp = "/" + packageName.replace('.', '/') + "/.*";
		path(Pattern.compile(regExp));
		return this;
	}

	public AFileName extension(String extension) {
		antPath("*." + extension);
		return this;
	}

	public AFileName name(Class<?> classToMatch) {
		String path = '/' + classToMatch.getSimpleName() + "\\.java";
		Package pkg = classToMatch.getPackage();
		if (pkg != null) {
			path = '/' + pkg.getName().replace('.', '/') + path;
		}
		path(Pattern.compile(path));
		return this;
	}
	
	public AFileName antName(String antPattern) {
		antPath("*/" + antPattern);
		return this;
	}

	public AFileName pkg(Class<?> classWithPkg) {
		pkg(classWithPkg.getPackage());
		return this;
	}

	public AFileName pkg(Package pkg) {
		antPath(pkg.toString().replace('.', '/'));
		return this;
	}
	
	public AFileName antPath(String antPattern) {
		withMatcher(AString.withAntPattern(antPattern));
		return this;
	}

	public AFileName path(Pattern pattern) {
		withMatcher(AString.withPattern(pattern));
		return this;
	}
}
