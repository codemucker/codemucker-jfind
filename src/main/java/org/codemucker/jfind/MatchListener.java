package org.codemucker.jfind;

/**
 * Be notified of matches and misses when filtering
 * @param <T>
 */
public interface MatchListener<T> {
	public void onMatched(T result);
	public void onIgnored(T result);
	/**
	 * Called on when error encountered
	 * @param record 
	 * @param e
	 */
	public void onError(Object record, Exception e) throws Exception;
}