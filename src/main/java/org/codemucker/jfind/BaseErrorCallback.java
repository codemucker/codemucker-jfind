package org.codemucker.jfind;

import org.codemucker.jfind.JFind.ErrorCallback;

public class BaseErrorCallback implements ErrorCallback {

	@Override
    public void onResourceError(RootResource resource, Exception e) {
    }

	@Override
    public void onClassError(RootResource resource,String fullClassname, Exception e) {
    }

	@Override
    public void onArchiveError(RootResource archive, Exception e) {
    }

	@Override
    public void onError(Object obj, Exception e) {
    }
}