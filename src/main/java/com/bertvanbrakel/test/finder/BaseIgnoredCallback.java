package com.bertvanbrakel.test.finder;

import com.bertvanbrakel.test.finder.ClassFinder.FinderIgnoredCallback;

public class BaseIgnoredCallback implements FinderIgnoredCallback {

	@Override
    public void onArchiveIgnored(RootResource archiveFile) {
    }

	@Override
    public void onResourceIgnored(RootResource resource) {
    }

	@Override
    public void onClassNameIgnored(String className) {
    }

	@Override
    public void onClassIgnored(Class<?> matchedClass) {
    }

	@Override
    public void onClassPathIgnored(Root root) {
    }

	@Override
    public void onIgnored(Object obj) {
    }
}