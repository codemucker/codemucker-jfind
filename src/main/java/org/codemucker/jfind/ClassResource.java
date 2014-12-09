package org.codemucker.jfind;

import com.google.common.base.Objects;

public class ClassResource {
    private final RootResource resource;
    private final String className;

    public ClassResource(RootResource resource, String className) {
        super();
        this.resource = resource;
        this.className = className;
    }

    public RootResource getResource() {
        return resource;
    }

    public String getClassName() {
        return className;
    }
    
    @Override
	public String toString(){
		return Objects
			.toStringHelper(this)
			.add("classname", className)
			.add("relresource", resource)
			.toString();	
	}
    
}