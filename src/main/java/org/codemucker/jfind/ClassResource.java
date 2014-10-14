package org.codemucker.jfind;

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
}