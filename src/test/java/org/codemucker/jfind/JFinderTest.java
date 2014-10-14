/*
 * Copyright 2011 Bert van Brakel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codemucker.jfind;

import static org.codemucker.jtest.TestUtils.list;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.codemucker.jfind.a.TstBeanOne;
import org.codemucker.jfind.b.TstBeanTwo;
import org.codemucker.jfind.c.TstAnonymous;
import org.codemucker.jfind.d.TstInner;
import org.codemucker.jfind.e.TstAnnotation;
import org.codemucker.jfind.e.TstAnnotationBean;
import org.codemucker.jfind.matcher.AClass;
import org.codemucker.jfind.matcher.AResource;
import org.codemucker.jfind.matcher.ARootResource;
import org.codemucker.jmatch.AString;
import org.junit.Test;


public class JFinderTest {

//	@Test
//	public void test_find_class_dir() {
//		ClassFinder.newBuilder().build();
//		ClassFinder finder = new ClassFinder();
//		File dir = finder.findClassesDir();
//		assertNotNull(dir);
//		String path = convertToForwardSlashes(dir.getPath());
//		assertTrue(path.endsWith("/target/classes"));
//	}

//	private String convertToForwardSlashes(String path) {
//		return path.replace('\\', '/');
//	}
	

	@Test
	public void test_find_classes_dir() {
		JFind finderDefault = newFinderBuilder()
			.build();
			
		JFind finderWithout = newFinderBuilder()
				.roots(Roots.with().build())
			.build();
		
		JFind finderWith = newFinderBuilder()
				.roots(Roots.with()
					.mainCompiledDir(true)
					.testCompiledDir(true).build())
			.build();

		Collection<Class<?>> foundDefault = list(finderDefault.findClasses());
		assertNotNull(foundDefault);
		assertTrue(foundDefault.contains(JFind.class));
		assertTrue(foundDefault.contains(JFinderTest.class));
		
		Collection<Class<?>> foundWithout = list(finderWithout.findClasses());
		assertNotNull(foundWithout);
		assertFalse(foundWithout.contains(JFind.class));
		assertFalse(foundWithout.contains(JFinderTest.class));
		
		Collection<Class<?>> foundWith = list(finderWith.findClasses());
		assertNotNull(foundWith);
		assertTrue(foundWith.contains(JFind.class));
		assertTrue(foundWith.contains(JFinderTest.class));
	}
	
	@Test
	public void test_find_test_classes() {
		JFind finderNoTests = newFinderBuilder()
			.roots(Roots.with().mainCompiledDir(true).build())
			.build();
		
		JFind finderTests = newFinderBuilder()
			.roots(Roots.with()
				.mainCompiledDir(true)
				.testCompiledDir(true)
				.build()
			)
			.build();

		Collection<Class<?>> foundNoTests = list(finderNoTests.findClasses());
		assertTrue(foundNoTests.contains(JFind.class));
		assertFalse(foundNoTests.contains(JFinderTest.class));
		
		Collection<Class<?>> foundTests = list(finderTests.findClasses());		
		assertTrue(foundTests.contains(JFind.class));
		assertTrue(foundTests.contains(JFinderTest.class));
	}
	
	@Test
	public void test_filename_exclude(){
		JFind finderWith = newFinderBuilder()
			.build();
			
		JFind finderWithout = newFinderBuilder()
		        .excludeResource(AResource.with().pathMatchingAntPattern("**Exception*.class"))
			.build();
		
		Collection<Class<?>> foundWith = list(finderWith.findClasses());		
		assertTrue(foundWith.contains(JFind.class));
		assertTrue(foundWith.contains(JFindException.class));

		Collection<Class<?>> foundWithout = list(finderWithout.findClasses());
		assertTrue(foundWithout.contains(JFind.class));
		assertFalse(foundWithout.contains(JFindException.class));
	}
	
	@Test
	public void test_filename_exclude_pkg(){
		JFind finderWith = newFinderBuilder()
			.build();
			
		JFind finderWithout = newFinderBuilder()
			.excludeResource(ARootResource.with().path(AString.matchingAntPattern("**/b/**")))
			.build();

		Collection<Class<?>> foundWith = list(finderWith.findClasses());
		assertTrue(foundWith.contains(JFind.class));
		assertTrue(foundWith.contains(TstBeanOne.class));		
		assertTrue(foundWith.contains(TstBeanTwo.class));
		
		Collection<Class<?>> foundWithout = list(finderWithout.findClasses());
		assertTrue(foundWithout.contains(JFind.class));
		assertTrue(foundWithout.contains(TstBeanOne.class));		
		assertFalse(foundWithout.contains(TstBeanTwo.class));
	}

	@Test
	public void test_filename_exclude_target_has_no_effect(){
		JFind finder = newFinderBuilder()
			.excludeResource(ARootResource.with().path(AString.matchingAntPattern("**/target/**")))
			.build();
		
		Collection<Class<?>> found = list(finder.findClasses());

		assertTrue(found.contains(JFind.class));
	}
	
	@Test
	public void test_filename_include(){
		JFind finderNoInclude = newFinderBuilder()
			.build();
			
		JFind finderInclude = newFinderBuilder()
			.includeResource(ARootResource.with().path("*/a/*"))
			.build();

		Collection<Class<?>> foundNoInclude = list(finderNoInclude.findClasses());
		assertTrue(foundNoInclude.contains(TstBeanTwo.class));
		
		Collection<Class<?>> foundInclude = list(finderInclude.findClasses());
		assertFalse(foundInclude.contains(TstBeanTwo.class));
	}
	
	@Test
	public void test_filename_include_multiple_packages(){
		JFind finder = newFinderBuilder()
			.includeResource(ARootResource.with().path(AString.matchingAntPattern("**/a/**")))
			.includeResource(ARootResource.with().path(AString.matchingAntPattern("**/b/**")))
			.build();
		
		Collection<Class<?>> found = list(finder.findClasses());

		assertTrue(found.contains(TstBeanTwo.class));
	}
	
	@Test
	public void test_filename_exclude_trumps_include(){
		JFind finder = newFinderBuilder()
			.includeResource(ARootResource.with().path(AString.matchingAntPattern("**/a/**")))
			.excludeResource(ARootResource.with().path(AString.matchingAntPattern("**/a/**")))
			.build();
			
		Collection<Class<?>> found = list(finder.findClasses());

		assertFalse(found.contains(TstBeanOne.class));
	}
	
	@Test
	public void test_include_instance_of(){
		JFind finder = newFinderBuilder()
			.includeClass(AClass.that().isASubclassOf(TstInterface1.class))
			.build();
		
		Collection<Class<?>> found = list(finder.findClasses());

		assertTrue(found.contains(TstInterface1.class));
		assertTrue(found.contains(TstBeanOne.class));
		assertTrue(found.contains(TstBeanOneAndTwo.class));
		
		assertEquals(3, found.size());
	}

	@Test
	public void test_multiple_implements(){
		JFind finder = newFinderBuilder()
			.includeClass(AClass.that().isASubclassOf(TstInterface1.class))
			.includeClass(AClass.that().isASubclassOf(TstInterface2.class))	
			.build();
		
		Collection<Class<?>> found = list(finder.findClasses());

		assertTrue(found.contains(TstInterface1.class));
		assertTrue(found.contains(TstInterface2.class));
		assertTrue(found.contains(TstBeanOne.class));
		assertTrue(found.contains(TstBeanTwo.class));
		assertTrue(found.contains(TstBeanOneAndTwo.class));
		
		assertEquals(5, found.size());
	}

	@Test
	public void test_class_must_match_multiple_matchers(){
		JFind finder = newFinderBuilder()
			.includeClass(AClass.all(
					AClass.that().isASubclassOf(TstInterface1.class),
					AClass.that().isASubclassOf(TstInterface2.class)))	
			.build();
		
		Collection<Class<?>> found = list(finder.findClasses());

		assertTrue(found.contains(TstBeanOneAndTwo.class));

		assertEquals(1, found.size());
	}

	@Test
	public void test_find_enums(){
		JFind finder = newFinderBuilder().build();
		
		Collection<Class<?>> found = list(finder.findClasses());

		assertTrue(found.contains(TstEnum.class));
		assertTrue(found.contains(TstBeanOneAndTwo.InstanceEnum.class));
		assertTrue(found.contains(TstBeanOneAndTwo.StaticEnum.class));
	}
	
	@Test
	public void test_filter_enum(){
		JFind finder = newFinderBuilder()
		    .excludeClass(AClass.that().isEnum())
			.build();
		
		Collection<Class<?>> found = list(finder.findClasses());

		assertFalse(found.contains(TstEnum.class));
		assertFalse(found.contains(TstBeanOneAndTwo.InstanceEnum.class));
		assertFalse(found.contains(TstBeanOneAndTwo.StaticEnum.class));

		assertTrue(found.size() > 1);
	}
	
	@Test
	public void test_filter_anonymous(){
		JFind finder = newFinderBuilder()
			.includeResource(ARootResource.with().path(AString.matchingAntPattern("**/c/**")))
			.excludeClass(AClass.that().isAnonymous())
			.build();
		
		Collection<Class<?>> found = list(finder.findClasses());

		assertEquals(list(TstAnonymous.class),list(found));
	}
	
	@Test
	public void test_filter_inner_class(){
		JFind finder = newFinderBuilder()
			.includeResource(ARootResource.with().path(AString.matchingAntPattern("**/d/**")))
			.excludeClass(AClass.that().isInnerClass())
			.build();
		
		Collection<Class<?>> found = list(finder.findClasses());

		assertEquals(list(TstInner.class),list(found));
	}	
	
	@Test
	public void test_filter_interfaces(){
		JFind finder = newFinderBuilder()
			.excludeClass(AClass.that().isInterface())
			.build();
		
		Collection<Class<?>> found = list(finder.findClasses());
		
		assertFalse(found.contains(TstInterface.class));
		assertFalse(found.contains(TstInterface1.class));
		assertFalse(found.contains(TstInterface2.class));

		assertTrue(found.contains(TstBeanOneAndTwo.class));
	}
	
	@Test
	public void test_find_has_annotations(){
		JFind finder = newFinderBuilder()			
			.includeClass(AClass.with().annotation(TstAnnotation.class))
			.build();

		Collection<Class<?>> found = list(finder.findClasses());

		assertEquals(list(TstAnnotationBean.class), found);
	}
	
	private static Criteria newFinderBuilder(){
		return Criteria.with()
            			.roots(Roots.with()
            				.mainCompiledDir(true)
            				.testCompiledDir(true)
            				.build()
			);
	}

}
