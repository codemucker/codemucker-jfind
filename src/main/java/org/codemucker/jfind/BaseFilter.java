package org.codemucker.jfind;

import org.codemucker.jfind.ClassScanner.Filter;

public class BaseFilter implements Filter {

    @Override
    public boolean isIncludeRoot(Root root) {
        return true;
    }
    
    @Override
    public boolean isIncludeResource(RootResource resource) {
        return true;
    }

	@Override
    public boolean isIncludeClass(RootResource resource,Class<?> classToMatch) {
        return true;
    }

	@Override
    public boolean isIncludeArchive(RootResource archiveFile) {
        return true;
    }

	@Override
    public boolean isInclude(Object obj) {
	    return true;
    }

    
}