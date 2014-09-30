package org.codemucker.jfind;

import org.codemucker.jmatch.AbstractNotNullMatcher;
import org.codemucker.jmatch.Description;
import org.codemucker.jmatch.MatchDiagnostics;
import org.codemucker.jmatch.Matcher;

import com.google.common.base.Preconditions;

public class MatcherToFindFilterAdapter<T> extends AbstractNotNullMatcher<T> implements FindResult.Filter<T> {
	
	private final Matcher<T> matcher;

	public static <T> FindResult.Filter<T> from(Matcher<T> matcher){
		return new MatcherToFindFilterAdapter<T>(matcher);
	}
	
	private MatcherToFindFilterAdapter(Matcher<T> matcher){
		Preconditions.checkNotNull(matcher, "expect non null matcher");
		this.matcher = matcher;
	}
	
	@Override
	public boolean matchesSafely(T found, MatchDiagnostics ctxt) {
		return ctxt.tryMatch(this,found,matcher);
	}

	@Override
	public void onMatched(T result) {
		//do nothing
	}

	@Override
	public void onIgnored(T result) {
		//do nothing
	}

	@Override
	public void onError(Object record, Exception e) throws Exception { 
		throw e;
	}
	
	@Override
	public void describeTo(Description desc) {
		super.describeTo(desc);
		matcher.describeTo(desc);
	}
}