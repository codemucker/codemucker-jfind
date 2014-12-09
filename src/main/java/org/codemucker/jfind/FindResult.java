package org.codemucker.jfind;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codemucker.jmatch.MatchDiagnostics;
import org.codemucker.jmatch.Matcher;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * Instead of returning lists of things, use this instead. This provides support for
 * filtering, transforming, converting results.
 *
 * @param <T>
 */
public interface FindResult<T> extends Iterable<T> {

	public boolean isEmpty();
	
	public List<T> toList();

	/**
	 * Return a new view over the current results using the given filter. Anything that matches
	 * the filter is kept, non matching items are removed
	 * @param matcher used to filter the original results. Return true if the item should be retained
	 * @return
	 */
	public FindResult<T> filter(Matcher<T> matcher);
	public FindResult<T> filter(Matcher<T> matcher, MatchListener<? super T> listener);
    public FindResult<T> filter(Matcher<T> matcher, MatchListener<? super T> listener, MatchDiagnostics diagnostics);
	
	/**
	 * Return a new view over the current results using the given predicate. If the predicate
	 * return true for a given item it is retained, else it is excluded
	 * @param predicate used to filter the original results. Return true if the item should be retained
	 * @return
	 */
	public FindResult<T> filter(Predicate<T> predicate);

	/**
	 * Return a new view over the current results using the given filter. If the filter
	 * returns true for a given item it is retained, else it is excluded
	 * @param filter used to filter the original results. Return true if the item should be retained
	 * @return
	 */
	public FindResult<T> filter(Filter<T> filter);
	
	public <B> FindResult<B> transform(Function<T, B> transformFunc);
	
	public <B> FindResult<B> transformToMany(Function<T, Iterator<B>> transformFunc);
	
	/**
	 * Return the results as a map using the given key provider to generate keys
	 * @param keyProvider
	 * @return
	 */
	public <K> Map<K,T> toMap(KeyProvider<K, T> keyProvider);

	public T getFirst();
	
	public T getFirstOrNull();
	
	public static interface KeyProvider<K,V> {
		public K getKeyFor(V value);
	}
	
	public interface Filter<T> extends Matcher<T>, MatchListener<T>{
	}
}
