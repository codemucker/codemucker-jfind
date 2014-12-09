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
import org.codemucker.jfind.matcher.ARootResource;
import org.codemucker.jfind.matcher.ARootResource;
import org.codemucker.jmatch.AString;
import org.junit.Test;


public class ClassScannerTest {

	@Test
	public void test_find_classes_dir() {
		ClassScanner finderDefault = newFinderBuilder()
			.build();
			
		ClassScanner finderWithout = newFinderBuilder()
				.scanRoots(Roots.with().build())
			.build();
		
		ClassScanner finderWith = newFinderBuilder()
				.scanRoots(Roots.with()
					.mainCompiledDir(true)
					.testCompiledDir(true).build())
			.build();

		Collection<Class<?>> foundDefault = list(finderDefault.findClasses());
		assertNotNull(foundDefault);
		assertTrue(foundDefault.contains(ClassScanner.class));
		assertTrue(foundDefault.contains(ClassScannerTest.class));
		
		Collection<Class<?>> foundWithout = list(finderWithout.findClasses());
		assertNotNull(foundWithout);
		assertFalse(foundWithout.contains(ClassScanner.class));
		assertFalse(foundWithout.contains(ClassScannerTest.class));
		
		Collection<Class<?>> foundWith = list(finderWith.findClasses());
		assertNotNull(foundWith);
		assertTrue(foundWith.contains(ClassScanner.class));
		assertTrue(foundWith.contains(ClassScannerTest.class));
	}
	
	@Test
	public void test_find_test_classes() {
		ClassScanner finderNoTests = newFinderBuilder()
			.scanRoots(Roots.with().mainCompiledDir(true).build())
			.build();
		
		ClassScanner finderTests = newFinderBuilder()
			.scanRoots(Roots.with()
				.mainCompiledDir(true)
				.testCompiledDir(true)
				.build()
			)
			.build();

		Collection<Class<?>> foundNoTests = list(finderNoTests.findClasses());
		assertTrue(foundNoTests.contains(ClassScanner.class));
		assertFalse(foundNoTests.contains(ClassScannerTest.class));
		
		Collection<Class<?>> foundTests = list(finderTests.findClasses());		
		assertTrue(foundTests.contains(ClassScanner.class));
		assertTrue(foundTests.contains(ClassScannerTest.class));
	}
	
	@Test
	public void test_filename_exclude(){
		ClassScanner finderWith = newFinderBuilder()
			.build();
			
		ClassScanner finderWithout = newFinderBuilder()
		        .filter(ClassFilter.with()
		                .resourceMatches(ARootResource.not(ARootResource.with().pathMatchingAntPattern("**Exception*.class"))))
			.build();
		
		Collection<Class<?>> foundWith = list(finderWith.findClasses());		
		assertTrue(foundWith.contains(ClassScanner.class));
		assertTrue(foundWith.contains(JFindException.class));

		Collection<Class<?>> foundWithout = list(finderWithout.findClasses());
		assertTrue(foundWithout.contains(ClassScanner.class));
		assertFalse(foundWithout.contains(JFindException.class));
	}
	
	@Test
	public void test_filename_exclude_pkg(){
		ClassScanner finderWith = newFinderBuilder()
			.build();
			
		ClassScanner finderWithout = newFinderBuilder()
		        .filter(ClassFilter.with()
                        .resourceMatches(ARootResource.not(ARootResource.with().path(AString.matchingAntPattern("**/b/**")))))
			.build();

		Collection<Class<?>> foundWith = list(finderWith.findClasses());
		assertTrue(foundWith.contains(ClassScanner.class));
		assertTrue(foundWith.contains(TstBeanOne.class));		
		assertTrue(foundWith.contains(TstBeanTwo.class));
		
		Collection<Class<?>> foundWithout = list(finderWithout.findClasses());
		assertTrue(foundWithout.contains(ClassScanner.class));
		assertTrue(foundWithout.contains(TstBeanOne.class));		
		assertFalse(foundWithout.contains(TstBeanTwo.class));
	}

	@Test
	public void test_filename_exclude_target_has_no_effect(){
		ClassScanner finder = newFinderBuilder()
		        .filter(ClassFilter.with()
                        .resourceMatches(ARootResource.not(ARootResource.with().path(AString.matchingAntPattern("**/target/**")))))
			.build();
		
		Collection<Class<?>> found = list(finder.findClasses());

		assertTrue(found.contains(ClassScanner.class));
	}
	
	@Test
	public void test_filename_include(){
		ClassScanner finderNoInclude = newFinderBuilder()
			.build();
			
		ClassScanner finderInclude = newFinderBuilder()
		        .filter(ClassFilter.with()
                        .resourceMatches(ARootResource.with().path("*/a/*")))
			.build();

		Collection<Class<?>> foundNoInclude = list(finderNoInclude.findClasses());
		assertTrue(foundNoInclude.contains(TstBeanTwo.class));
		
		Collection<Class<?>> foundInclude = list(finderInclude.findClasses());
		assertFalse(foundInclude.contains(TstBeanTwo.class));
	}
	
	@Test
	public void test_filename_include_multiple_packages(){
		ClassScanner finder = newFinderBuilder()
		        .filter(ClassFilter.with()
                        .resourceMatches(ARootResource.any(
                                ARootResource.with().path(AString.matchingAntPattern("**/a/**")),
                                ARootResource.with().path(AString.matchingAntPattern("**/b/**")))))
			.build();
		
		Collection<Class<?>> found = list(finder.findClasses());

		assertTrue(found.contains(TstBeanTwo.class));
	}

	@Test
	public void test_include_instance_of(){
		ClassScanner finder = newFinderBuilder()
                .filter(ClassFilter.with()
                        .classMatches(AClass.that().isASubclassOf(TstInterface1.class)))
			.build();
		
		Collection<Class<?>> found = list(finder.findClasses());

		assertTrue(found.contains(TstInterface1.class));
		assertTrue(found.contains(TstBeanOne.class));
		assertTrue(found.contains(TstBeanOneAndTwo.class));
		
		assertEquals(3, found.size());
	}

	@Test
	public void test_multiple_implements(){
		ClassScanner finder = newFinderBuilder()
	              .filter(ClassFilter.with()
                     .classMatches(AClass.any(
                         AClass.that().isASubclassOf(TstInterface1.class),
                         AClass.that().isASubclassOf(TstInterface2.class))))
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
		ClassScanner finder = newFinderBuilder()
		        .filter(ClassFilter.with()
		                 .classMatches(AClass.all(
		                         AClass.that().isASubclassOf(TstInterface1.class),
		                         AClass.that().isASubclassOf(TstInterface2.class)))	)
			.build();
		
		Collection<Class<?>> found = list(finder.findClasses());

		assertTrue(found.contains(TstBeanOneAndTwo.class));

		assertEquals(1, found.size());
	}

	@Test
	public void test_find_enums(){
		ClassScanner finder = newFinderBuilder().build();
		
		Collection<Class<?>> found = list(finder.findClasses());

		assertTrue(found.contains(TstEnum.class));
		assertTrue(found.contains(TstBeanOneAndTwo.InstanceEnum.class));
		assertTrue(found.contains(TstBeanOneAndTwo.StaticEnum.class));
	}
	
	@Test
	public void test_filter_enum(){
		ClassScanner finder = newFinderBuilder()
		    .filter(ClassFilter.with()
		             .classMatches(AClass.that().isNotEnum()))
			.build();
		
		Collection<Class<?>> found = list(finder.findClasses());

		assertFalse(found.contains(TstEnum.class));
		assertFalse(found.contains(TstBeanOneAndTwo.InstanceEnum.class));
		assertFalse(found.contains(TstBeanOneAndTwo.StaticEnum.class));

		assertTrue(found.size() > 1);
	}
	
	@Test
	public void test_filter_anonymous(){
		ClassScanner finder = newFinderBuilder()
		   .filter(ClassFilter.with()
		                .resourceMatches(ARootResource.with().path(AString.matchingAntPattern("**/c/**")))
		                .classMatches(AClass.that().isNotAnonymous()))
			.build();
		
		Collection<Class<?>> found = list(finder.findClasses());

		assertEquals(list(TstAnonymous.class),list(found));
	}
	
	@Test
	public void test_filter_inner_class(){
		ClassScanner finder = newFinderBuilder()
			.filter(ClassFilter.with()
			            .resourceMatches(ARootResource.with().path(AString.matchingAntPattern("**/d/**")))
			            .classMatches(AClass.that().isNotInner()))
			.build();
		
		Collection<Class<?>> found = list(finder.findClasses());

		assertEquals(list(TstInner.class),list(found));
	}	
	
	@Test
	public void test_filter_interfaces(){
		ClassScanner finder = newFinderBuilder()
			.filter(ClassFilter.with().classMatches(AClass.that().isNotInterface()))
			.build();
		
		Collection<Class<?>> found = list(finder.findClasses());
		
		assertFalse(found.contains(TstInterface.class));
		assertFalse(found.contains(TstInterface1.class));
		assertFalse(found.contains(TstInterface2.class));

		assertTrue(found.contains(TstBeanOneAndTwo.class));
	}
	
	@Test
	public void test_find_has_annotations(){
		ClassScanner finder = newFinderBuilder()			
			.filter(ClassFilter.with()
			        .classMatches(AClass.with().annotation(TstAnnotation.class)))
			.build();

		Collection<Class<?>> found = list(finder.findClasses());

		assertEquals(list(TstAnnotationBean.class), found);
	}
	
	private static ClassScanner.Builder newFinderBuilder(){
		return ClassScanner.with()
            			.scanRoots(Roots.with()
            				.mainCompiledDir(true)
            				.testCompiledDir(true)
            				.build()
			);
	}

}
