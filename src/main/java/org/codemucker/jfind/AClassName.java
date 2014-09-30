package org.codemucker.jfind;

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
	    super(String.class);
	}
	
    public static Matcher<String> any() {
    	return Logical.any();
    }
    
    public static Matcher<String> none() {
    	return Logical.none();
    }

    public AClassName packageName(String packageName) {
		String regExp = packageName.replaceAll("\\.", "\\.") + "\\.[^.]*";
		nameRegexMatch(regExp);
		return this;
    }
    
    public AClassName packageStartingWith(String packageName) {
		String regExp = packageName.replaceAll("\\.", "\\.") + "\\..*";
		nameRegexMatch(regExp);
		return this;
	}
    
    public AClassName nameAntPatternMatch(final String nameAntPattern){
    	addMatcher(AString.matchingAntPattern(nameAntPattern));
    	return this;
    }

    public AClassName nameRegexMatch(final String namePattern){
    	addMatcher(AString.matchingRegex(namePattern));
    	return this;
    }

    public AClassName nameRegexMatch(Pattern pattern){
    	addMatcher(AString.matchingRegex(pattern));
    	return this;
    }
    
    public AClassName name(final String name){
    	addMatcher(AString.equalTo(name));
    	return this;
    }
}
