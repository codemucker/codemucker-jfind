package org.codemucker.jfind;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.codemucker.lang.Check.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.codemucker.jmatch.MatchDiagnostics;
import org.codemucker.jmatch.Matcher;
import org.codemucker.jmatch.NullMatchContext;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;

public class DefaultFindResult<T> implements FindResult<T> {

	private final Iterable<T> source;
	private Boolean empty;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static final DefaultFindResult EMPTY = new DefaultFindResult(Collections.emptyList());
	
	@SuppressWarnings("unchecked")
	public static <T> DefaultFindResult<T> emptyResults(){
		return EMPTY;
	}

	public static <T> DefaultFindResult<T> from(Iterable<T> source){
		return new DefaultFindResult<T>(source);
	}
	
	public static <T> DefaultFindResult<T> from(Iterator<T> source){
		return new DefaultFindResult<T>(toIterable(source));
	}
	
	private static <T> Iterable<T> toIterable(Iterator<T> iter){
		List<T> list = newArrayList();
		while( iter.hasNext()){
			T ele = iter.next();
			if( ele != null){
			    list.add(ele);
			}
		}
		return list;
	}
	
	/**
	 * @param results can be null in which case it is treated as an empty list
	 */
	@SuppressWarnings("unchecked")
    public DefaultFindResult(Iterable<T> results) {
	    super();
	    this.source = results==null?Collections.EMPTY_LIST:results;
    }

	@Override
    public Iterator<T> iterator() {
	    return source.iterator();
    }

	@Override
	public boolean isEmpty(){
		if( empty == null){
			if( source instanceof Collection){
				empty = ((Collection<?>)source).isEmpty();
			} else {
				empty = source.iterator().hasNext();
			}
		}
		return empty.booleanValue();
	}
	
	@Override
    public List<T> toList() {
		if( source instanceof List){
			return (List<T>)source;
		} else {
			return newArrayList(source);
		}
	}

	@Override
    public FindResult<T> filter(Matcher<T> matcher) {
		return filter(MatcherToFindFilterAdapter.from(matcher));
    }

	@Override
    public FindResult<T> filter(Predicate<T> predicate) {
		return filter(PredicateToFindFilterAdapter.from(predicate));
    }

	@Override
	public FindResult<T> filter(FindResult.Filter<T> filter) {
		return filter(filter,filter, NullMatchContext.INSTANCE);
	}
	
	@Override
    public FindResult<T> filter(Matcher<T> matcher,MatchListener<? super T> listener) {
        return filter(matcher,listener, NullMatchContext.INSTANCE);
    }
    
	@Override
    public FindResult<T> filter(Matcher<T> matcher, MatchListener<? super T> listener, MatchDiagnostics diagnostics) {
		return from(FilteringIterator.from(source.iterator(), matcher, listener, diagnostics==null?NullMatchContext.INSTANCE:diagnostics));
    }

	@Override
    public <K> Map<K, T> toMap(KeyProvider<K, T> maker) {
		Map<K, T> map = newHashMap();
		for(T item:this){
			K key = maker.getKeyFor(item);
			map.put(key, item);
		}
	    return map;
    }

	@Override
    public T getFirst() {	
	    return iterator().next();
	}
	
	@Override
	public T getFirstOrNull() {
		Iterator<T> iter = iterator();
		if (iter.hasNext()) {
			return iter.next();
		}
		return null;
	}
	
	@Override
	public <B> FindResult<B> transform(Function<T, B> transformFunc) {
		return from(SingleTransformIterator.from(this.iterator(),transformFunc));
	}

	@Override
	public <B> FindResult<B> transformToMany(
			Function<T, Iterator<B>> transformFunc) {
		return from(ExpandingTransformIterator.from(this.iterator(),transformFunc));
	}
	
	@Override
	public String toString() {
		return Objects
			.toStringHelper(getClass())
			.add("results", this.source)
			.toString();
	}
	
	private static class FilteringIterator<T> implements Iterator<T> {

		private final Iterator<T> source;
		private final Matcher<T> matcher;
		private final MatchListener<? super T> listener;
		private final MatchDiagnostics diagnostics;
        
		private T nextItem;
		
		private boolean init = true;

		static <T> FilteringIterator<T> from(Iterator<T> source, Matcher<T> matcher, MatchListener<? super T> listener, MatchDiagnostics diagnostics){
			return new FilteringIterator<T>(source,matcher, listener, diagnostics);
		}
		
		/**
		 * @param source the backing iterator which provides the item to iterate over
		 * @param filter the filter which filters the source iterator
		 */
		public FilteringIterator(Iterator<T> source, Matcher<T> matcher, MatchListener<? super T> listener, MatchDiagnostics diagnostics) {
			checkNotNull("soure", source);
			checkNotNull("matcher", matcher);
			checkNotNull("listener", listener);
			
			this.source = source;
			this.matcher = matcher;
			this.listener = listener;
			this.diagnostics = diagnostics;
		}

		@Override
		public T next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			T ret = nextItem;
			nextItem = nextItem();
			return ret;
		}

		@Override
		public boolean hasNext() {
			if(init){
				init = false;
				nextItem = nextItem();
			}
			return nextItem != null;
		}
		
		private T nextItem() {
			while (source.hasNext()) {
				T item = source.next();
				if (matcher.matches(item,diagnostics)) {
					listener.onMatched(item);
					return item;
				} else {
					listener.onIgnored(item);
				}
			}
			return null;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	private static class SingleTransformIterator<A,B> implements Iterator<B> {

		private final Iterator<A> source;
		private final Function<A, B> transformFunc;
		
		private B next;
		
		static <A,B> SingleTransformIterator<A,B> from(Iterator<A> source,Function<A, B> transformFunc){
			return new SingleTransformIterator<A,B>(source,transformFunc);
		}
		
		SingleTransformIterator(Iterator<A> source,Function<A, B> transformFunc) {
			checkNotNull("source", source);
			checkNotNull("transform", transformFunc);
			
			this.source = source;
			this.transformFunc = transformFunc;
			this.next = nextItem();
		}
		
		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public B next() {
			if (next == null) {
				throw new NoSuchElementException();
			}
			B ret = next;
			next = nextItem();
			return ret;
		}

		private B nextItem() {
			B next = null;
			while(next == null && source.hasNext()){
				next = transformFunc.apply(source.next());
			}
			return next;
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException("removals not supported");
		}	
	}
	
	private static class ExpandingTransformIterator<A,B> implements Iterator<B> {

		private final Iterator<A> source;
		private final Function<A, Iterator<B>> transformFunc;
		
		private Iterator<B> currentExpandedIterator;
		private B nextItem;

		static <A,B> ExpandingTransformIterator<A,B> from(Iterator<A> source,Function<A, Iterator<B>> transformFunc){
			return new ExpandingTransformIterator<A,B>(source,transformFunc);
		}
		
		public ExpandingTransformIterator(Iterator<A> source,Function<A, Iterator<B>> transformFunc) {
			checkNotNull("source", source);
			checkNotNull("transform", transformFunc);
			
			this.source = source;
			this.transformFunc = transformFunc;
			this.nextItem = nextItem();
		}
		
		@Override
		public boolean hasNext() {
			return nextItem != null;
		}

		@Override
		public B next() {
			if (nextItem == null) {
				throw new NoSuchElementException();
			}
			B ret = nextItem;
			nextItem = nextItem();
			return ret;
		}

		private B nextItem() {
			B next = null;
			//try to find the next item in the current iterator
			while(currentExpandedIterator != null && next == null && currentExpandedIterator.hasNext()){
				next = currentExpandedIterator.next();
			}
			//move on to the next iterator from the source
			while (next == null && source.hasNext()) {
				A sourceItem = source.next();
				currentExpandedIterator = transformFunc.apply(sourceItem);
				//try t find the next item in the return iterator
				while(currentExpandedIterator != null && next == null && currentExpandedIterator.hasNext()){
					next = currentExpandedIterator.next();
				}
			}
			return next;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("removals not supported");
		}	
	}
}