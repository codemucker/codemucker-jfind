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
	public void onError(Object record, Throwable t) throws Exception {
	}

	protected void onIgnored(Root record) {
	}

	protected void onIgnored(RootResource record) {
	}

	protected void onIgnored(ClassResource record) {
	}

	protected void onIgnored(Class<?> record) {
	}

	protected void onMatched(Root record) {
	}

	protected void onMatched(RootResource record) {
	}

	protected void onMatched(ClassResource record) {
	}

	protected void onMatched(Class<?> record) {
	}

}
