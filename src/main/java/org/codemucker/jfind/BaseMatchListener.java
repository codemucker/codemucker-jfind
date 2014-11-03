package org.codemucker.jfind;

public class BaseMatchListener<T> implements MatchListener<T> {

    @Override
    public void onMatched(T result) {
        if (result instanceof Root) {
            onMatched((Root) result);
        } else if (result instanceof RootResource) {
            onMatched((RootResource) result);
        } else if (result instanceof ClassResource) {
            onMatched((ClassResource) result);
        } else if (result instanceof Class) {
            onMatched((Class<?>) result);
        }
    }


    @Override
    public void onIgnored(T result) {
        if (result instanceof Root) {
            onIgnored((Root) result);
        } else if (result instanceof RootResource) {
            onIgnored((RootResource) result);
        } else if (result instanceof ClassResource) {
            onIgnored((ClassResource) result);
        } else if (result instanceof Class) {
            onIgnored((Class<?>) result);
        }
    }

    @Override
    public void onError(Object record, Exception e) throws Exception {
    }

    protected void onIgnored(Root result) {
    }

    protected void onIgnored(RootResource result) {
    }

    protected void onIgnored(ClassResource result) {
    }

    protected void onIgnored(Class<?> result) {
    }


    protected void onMatched(Root root) {
    }

    protected void onMatched(RootResource resource) {
    }

    protected void onMatched(ClassResource classResource) {
    }

    protected void onMatched(Class<?> klass) {
    }

    
}
