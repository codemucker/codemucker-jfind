package org.codemucker.jfind;

import static org.codemucker.jmatch.Assert.assertThat;
import static org.codemucker.jmatch.Assert.is;

import java.io.File;
import java.util.Collection;

import org.codemucker.jfind.a.TstBeanOne;
import org.codemucker.jfind.b.TstBeanTwo;
import org.codemucker.jfind.matcher.ARootResource;
import org.codemucker.jmatch.AList;
import org.codemucker.jtest.ProjectLayouts;
import org.junit.Test;

import com.google.common.collect.Lists;

public class DirectoryRootTest {

	@Test
	public void walkResourcesTest(){
		File dir = ProjectLayouts.getDefaultResolver().getBaseDir();
		Root root = new DirectoryRoot(new File(dir, "src/test/java/"));
		
		final Collection<RootResource> resources = Lists.newArrayList();
		root.accept(new BaseRootVisitor(){
			@Override
			public boolean visit(RootResource resource) {
				resources.add(resource);
				return true;
			}
		});
		
		assertThat(
			resources,
			is(AList.of(RootResource.class)
				.inAnyOrder()
				.withAtLeast()
				.item(ARootResource.with().pathFromClass(TstBeanOne.class))
				.item(ARootResource.with().pathFromClass(TstBeanTwo.class))
				.item(ARootResource.with().pathFromClass(DirectoryRootTest.class)))
		);
	}
}
