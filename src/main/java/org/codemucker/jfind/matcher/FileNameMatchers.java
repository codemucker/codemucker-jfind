package org.codemucker.jfind.matcher;

import java.util.regex.Pattern;

import org.codemucker.jmatch.AString;
import org.codemucker.jmatch.Logical;
import org.codemucker.jmatch.Matcher;



public class FileNameMatchers { //extends Logical {
	
    public static Matcher<String> any() {
    	return Logical.any();
    }
    
    public static Matcher<String> none() {
    	return Logical.none();
    }
    
	public Matcher<String> withPackage(String packageName) {
		String regExp = "/" + packageName.replace('.', '/') + "/.*";
		return withPath(Pattern.compile(regExp));
	}

	public static Matcher<String> withExtension(String extension) {
		return withAntPath("*." + extension);
	}

	public static Matcher<String> withName(Class<?> classToMatch) {
		String path = '/' + classToMatch.getSimpleName() + "\\.java";
		Package pkg = classToMatch.getPackage();
		if (pkg != null) {
			path = '/' + pkg.getName().replace('.', '/') + path;
		}
		return withPath(Pattern.compile(path));
	}
	
	public static Matcher<String> withAntName(String antPattern) {
		return withAntPath("*/" + antPattern);
	}

	public static Matcher<String> inPackage(Class<?> classWithPkg) {
		return inPackage(classWithPkg.getPackage());
	}

	public static Matcher<String> inPackage(Package pkg) {
		return withAntPath(pkg.toString().replace('.', '/'));
	}
	
	public static Matcher<String> withAntPath(String antPattern) {
		return AString.withAntPattern(antPattern);
	}

	public static Matcher<String> withPath(Pattern pattern) {
		return AString.withPattern(pattern);
	}
}
