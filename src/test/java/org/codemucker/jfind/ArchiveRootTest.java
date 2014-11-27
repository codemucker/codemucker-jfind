package org.codemucker.jfind;

import static org.codemucker.jmatch.Assert.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.codemucker.jfind.matcher.ARootResource;
import org.codemucker.jmatch.AList;
import org.junit.Test;

import com.google.common.collect.Lists;

public class ArchiveRootTest {

	@Test
	public void walkResourcesTest() throws Exception {
		File zipPath = File.createTempFile("test", ".jar");
		createZipFile(zipPath,"a/b/c/noslash","/d/e/f/withslash");
		
		Root root = new ArchiveRoot(zipPath);
		
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
			AList.of(RootResource.class)
				.inOrder()
				.withOnly()
				.item(ARootResource.with().path("/a/b/c/noslash"))
				.item(ARootResource.with().path("/d/e/f/withslash"))
		);
		
		zipPath.delete();
	}
	
	private void createZipFile(File path, String... relPaths) throws Exception {
		ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(path));
		for(String relPath:relPaths){
			zip.putNextEntry(new ZipEntry(relPath));
		}
		zip.close();
	}
	
}
