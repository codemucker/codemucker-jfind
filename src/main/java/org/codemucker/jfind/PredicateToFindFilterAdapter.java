package org.codemucker.jfind;

import org.codemucker.jmatch.AbstractNotNullMatcher;
import org.codemucker.jmatch.Description;
import org.codemucker.jmatch.MatchDiagnostics;
import org.codemucker.jmatch.Matcher;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

public class PredicateToFindFilterAdapter<T> extends AbstractNotNullMatcher<T> implements FindResult.Filter<T> {
	
	private final Predicate<T> predicate;

	public static <T> FindResult.Filter<T> from(Predicate<T> predicate){
		return new PredicateToFindFilterAdapter<T>(predicate);
	}
	
	private PredicateToFindFilterAdapter(Predicate<T> predicate){
		Preconditions.checkNotNull(predicate, "expect non null predicate");
		this.predicate = predicate;
	}
	
	@Override
	public boolean matchesSafely(T found, MatchDiagnostics ctxt) {
		return predicate.apply(found);
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
	public void onError(Object record, Throwable t) throws Throwable { 
		throw t;
	}
	
	@Override
	public void describeTo(Description desc) {
		super.describeTo(desc);
		desc.text("predicate", predicate);
	}
}