package org.codemucker.jfind;

import org.codemucker.jfind.JFind.IgnoredCallback;

public class BaseIgnoredCallback implements IgnoredCallback {

	@Override
    public void onArchiveIgnored(RootResource archiveFile) {
    }

	@Override
    public void onResourceIgnored(RootResource resource) {
    }

	@Override
    public void onClassNameIgnored(RootResource resource,String className) {
    }

	@Override
    public void onClassIgnored(RootResource resource,Class<?> matchedClass) {
    }

	@Override
    public void onRootIgnored(Root root) {
    }

	@Override
    public void onIgnored(Object obj) {
    }
}