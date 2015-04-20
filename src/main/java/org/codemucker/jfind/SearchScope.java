package org.codemucker.jfind;

public class SearchScope {
	
	public static final SearchScope PARENT = new SearchScope(1<<0);
	public static final SearchScope DIRECT = new SearchScope(1<<1);
	public static final SearchScope CHILDREN = new SearchScope(1<<2);
	
	public static final SearchScope DIRECT_AND_CHILDREN = DIRECT.and(CHILDREN);
	public static final SearchScope ALL = PARENT.and(DIRECT).and(CHILDREN);
	
	private final int flag;
	
	private SearchScope(int flag){
		this.flag = flag;
	}
	
	public SearchScope and(SearchScope limit){
		return new SearchScope(flag | limit.flag);
	}
	
	public SearchScope not(SearchScope limit){
		return new SearchScope(flag | ~limit.flag);
	}
	
	public boolean isSet(SearchScope limit){
		return (this.flag & limit.flag) != 0;
	}
	
}