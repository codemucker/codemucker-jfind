package org.codemucker.jfind;

import org.codemucker.jfind.JFind.MatchedCallback;

public class BaseMatchedCallback implements MatchedCallback {
	@Override
	public void onArchiveMatched(RootResource archiveFile) {
	}

	@Override
	public void onResourceMatched(RootResource resource) {
	}

	@Override
	public void onClassNameMatched(RootResource resource,String className) {
	}

	@Override
	public void onRootMatched(Root root) {
	}

	@Override
	public void onClassMatched(RootResource resource,Class<?> matchedClass) {
	}

	@Override
    public void onMatched(Object obj) {
    }
}