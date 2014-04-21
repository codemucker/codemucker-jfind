package org.codemucker.jfind;

import org.codemucker.jmatch.AbstractNotNullMatcher;
import org.codemucker.jmatch.Description;
import org.codemucker.jmatch.MatchDiagnostics;
import org.codemucker.jmatch.Matcher;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

public class PredicateMatcherAdapter<T> extends AbstractNotNullMatcher<T> {
	
	private final Predicate<T> predicate;

	public static <T> Matcher<T> from(Predicate<T> predicate){
		return new PredicateMatcherAdapter<T>(predicate);
	}
	
	private PredicateMatcherAdapter(Predicate<T> predicate){
		Preconditions.checkNotNull(predicate, "expect non null predicate");
		this.predicate = predicate;
	}
	
	@Override
	public boolean matchesSafely(T found, MatchDiagnostics ctxt) {
		return predicate.apply(found);
	}

	@Override
	public void describeTo(Description desc) {
		super.describeTo(desc);
		desc.text("predicate", predicate);
	}
}