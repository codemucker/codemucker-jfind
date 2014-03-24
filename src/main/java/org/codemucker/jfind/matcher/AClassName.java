package org.codemucker.jfind.matcher;

import java.util.regex.Pattern;

import org.codemucker.jmatch.AString;
import org.codemucker.jmatch.Logical;
import org.codemucker.jmatch.Matcher;
import org.codemucker.jmatch.ObjectMatcher;

public class AClassName extends ObjectMatcher<String> {
	
	public static AClassName with(){
		return new AClassName();
	}
	
	private AClassName(){
	}
	
    public static Matcher<String> any() {
    	return Logical.any();
    }
    
    public static Matcher<String> none() {
    	return Logical.none();
    }

    public AClassName withExactPackage(String packageName) {
		String regExp = packageName.replaceAll("\\.", "\\.") + "\\.[^.]*";
		withNamePattern(regExp);
		return this;
    }
    
    public AClassName withPackageStartingWith(String packageName) {
		String regExp = packageName.replaceAll("\\.", "\\.") + "\\..*";
		withNamePattern(regExp);
		return this;
	}
    
    public AClassName withNameAntPattern(final String nameAntPattern){
    	withMatcher(AString.withAntPattern(nameAntPattern));
    	return this;
    }

    public AClassName withNamePattern(final String namePattern){
    	withMatcher(AString.withPattern(namePattern));
    	return this;
    }

    public AClassName withNamePattern(Pattern pattern){
    	withMatcher(AString.withPattern(pattern));
    	return this;
    }
    
    public AClassName withName(final String name){
    	withMatcher(AString.equalTo(name));
    	return this;
    }
}
