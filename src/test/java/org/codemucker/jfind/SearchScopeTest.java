package org.codemucker.jfind;

import org.junit.Assert;
import org.junit.Test;

public class SearchScopeTest {

	@Test
	public void isSet(){
		Assert.assertTrue(SearchScope.DIRECT.isSet(SearchScope.DIRECT));
		Assert.assertFalse(SearchScope.DIRECT.isSet(SearchScope.CHILDREN));
		
		Assert.assertTrue(SearchScope.CHILDREN.isSet(SearchScope.CHILDREN));
		Assert.assertFalse(SearchScope.CHILDREN.isSet(SearchScope.DIRECT));
		
		Assert.assertTrue(SearchScope.CHILDREN.isSet(SearchScope.DIRECT_AND_CHILDREN));

	}
}
