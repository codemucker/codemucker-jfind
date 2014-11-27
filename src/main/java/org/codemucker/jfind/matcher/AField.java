package org.codemucker.jfind.matcher;

import java.lang.reflect.Field;

import org.codemucker.jmatch.AString;
import org.codemucker.jmatch.Matcher;

public class AField extends AbstractModiferMatcher<AField,Field>{

	public AField() {
		super(Field.class);
	}

	public static AField that(){
		return with();
	}
	
	public static AField with(){
		return new AField();
	}
	
    @Override
    protected int getModifier(Field instance) {
        return instance.getModifiers();
    }
    
	public AField name(String nameAntPattern){
		name(AString.matchingAntPattern(nameAntPattern));
		return this;
	}
	
	public AField name(Matcher<String> matcher){
		matchProperty("name", String.class, matcher);
		return this;
	}

}
