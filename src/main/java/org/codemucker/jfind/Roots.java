package org.codemucker.jfind;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newLinkedHashMap;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codemucker.jfind.Root.RootContentType;
import org.codemucker.jfind.Root.RootType;
import org.codemucker.jtest.ProjectLayout;
import org.codemucker.jtest.ProjectLayouts;
import org.codemucker.lang.IBuilder;

import com.google.common.collect.Sets;
import com.google.common.io.Files;

public final class Roots  {
	
	public static Builder with(){
		return new Builder();
	}
	
	public static Builder with(Iterable<Root> roots){
		return new Builder(roots);
	}
	
	public static class Builder implements IBuilder<List<Root>> {

		private final Map<String,Root> roots = newLinkedHashMap();
		
		//TODO:support multiple projects?
		private ProjectLayout projectLayout;
		
		//TODO: set all to false to force explicit include
		private boolean includeMainSrcDir = true;
		private boolean includeGeneratedSrcDir = false;
        private boolean includeTestSrcDir = false;
		private boolean includeTestGeneratedSrcDir = false;
		private boolean includeClasspath = false;
		
		private Set<String> archiveTypes = Sets.newHashSet("jar","zip","ear","war", "nar");
		private Set<String> ignoreTypes = Sets.newHashSet("so" /* unix libs */);

		private boolean includeMainCompiledDir = false;
		private boolean includeTestCompiledDir = false;

		private boolean ignoreUnknownRootTypes;	
		
		private Builder(){
			//prevent instantiation outside of builder method
		}
		
		private Builder(Iterable<Root> roots){
			//prevent instantiation outside of builder method
			roots(roots);
		}
		/**
		 * Return a mutable list of class path roots. CHanges in the builder are not reflected in the returned
		 * list (or vice versa)
		 */
		@Override
        public List<Root> build(){
			ProjectLayout resolver = toResolver();
			
			Builder copy = new Builder();
			copy.roots.putAll(roots);
			if (includeMainSrcDir) {
				copy.roots(resolver.getMainSrcDirs(),RootType.MAIN, RootContentType.SRC);
			}
			if (includeTestSrcDir) {
				copy.roots(resolver.getTestSrcDirs(),RootType.TEST, RootContentType.SRC);
			}
			if (includeGeneratedSrcDir) {
				copy.roots(resolver.getGeneratedSrcDirs(),RootType.MAIN, RootContentType.SRC);
			}
			
			if (includeMainCompiledDir) {
				copy.roots(resolver.getMainCompileTargetDirs(),RootType.MAIN, RootContentType.BINARY);
			}
			if (includeTestCompiledDir) {
				copy.roots(resolver.getTestCompileTargetDirs(),RootType.TEST, RootContentType.BINARY);
			}
			if (includeTestGeneratedSrcDir) {
                copy.roots(resolver.getTestGeneratedSrcDirs(),RootType.TEST, RootContentType.SRC);
            }
            
			if (includeClasspath) {
				copy.roots(findClassPathDirs());
			}
			
			return newArrayList(copy.roots.values());
		}
		
		private ProjectLayout toResolver(){
			return projectLayout != null ? projectLayout : ProjectLayouts.getDefaultResolver();
		}
		
		public Builder copyOf() {
			Builder copy = new Builder();
			copy.projectLayout = projectLayout;
			copy.includeMainSrcDir = includeMainSrcDir;
			copy.includeClasspath = includeClasspath;
			copy.includeGeneratedSrcDir = includeGeneratedSrcDir;
			copy.includeTestSrcDir = includeTestSrcDir;
			copy.includeMainCompiledDir = includeMainCompiledDir;
			copy.includeTestCompiledDir = includeTestCompiledDir;
			copy.roots.putAll(roots);
			copy.archiveTypes.addAll(archiveTypes);
			
			return copy;
		}
		
		/**
		 * Try our hardest to find all the class path roots. 
		 * 
		 * <p>Walk both the classloaders and the lookup the system class path property
		 * as tools like maven set the system property but don't expose the class loader for dependencies, while tools like eclipse don't
		 * update the system property but does provide a useful class loader hierarchy</p>
		 * 
		 * @return all the paths to the jars, sources etc which could be found, in an attempt at the same order as what is used by the classloaders (though not guaranteed)
		 */
		private Collection<File> findClassPathDirs() {
		    Map<String,File> files = newLinkedHashMap();
		    findClassPathDirsFromClassLoaders(Thread.currentThread().getContextClassLoader(),files);
		    findClassPathDirsFromSystemProperty(files);
		    return files.values();
		}

        private void findClassPathDirsFromClassLoaders(ClassLoader classloader,Map<String, File> files) {
            List<URL> urls = new ArrayList<>();

            while (classloader != null) {
                if (classloader instanceof URLClassLoader) {
                    for (URL url : ((URLClassLoader) classloader).getURLs()) {
                        urls.add(url);
                        // System.out.println("Roots:url=" + url.getPath());
                    }
                } else {
                    log("ignoring classloader of type:" + classloader.getClass().getName());
                }
                classloader = classloader.getParent();
            }
            //most classloaders defer to parent loaders first. Therefore parent loaders classpath entries should come first.
            //as we walk up the chain we need to reverse the collected results
            Collections.reverse(urls);

            //convert to file paths
            for (URL url : urls) {
                try {
                    if ("file".equals(url.getProtocol())) {
                        File f = new File(url.getPath());
                        if (f.exists() & f.canRead()) {
                            String fullPath = f.getCanonicalPath();
                            if (!files.containsKey(fullPath)) {
                                files.put(fullPath, f);
                            }
                        } else {
                            log("can't read url:" + url);
                        }
                    } else {
                        log("skipping url:" + url);
                    }
                } catch (IOException e) {
                    throw new JFindException("Error trying to resolve pathname " + url);
                }
            }
        }

        private static void log(String msg) {
            //System.out.println(Roots.class.getSimpleName() + ": [DEBUG] " + msg);
        }

        private void findClassPathDirsFromSystemProperty(Map<String, File> files) {

            String classpath = System.getProperty("java.class.path");
            String sep = System.getProperty("path.separator");
            String[] paths = classpath.split(sep);

            for (String path : paths) {
                try {
                    File f = new File(path);
                    if (f.exists() & f.canRead()) {
                        String fullPath = f.getCanonicalPath();
                        if (!files.containsKey(fullPath)) {
                            files.put(fullPath, f);
                        }
                    }
                } catch (IOException e) {
                    throw new JFindException("Error trying to resolve pathname " + path);
                }
            }
        }

		public Builder projectLayout(ProjectLayout layout){
			this.projectLayout = layout;
			return this;
		}
		
		public Builder archiveFileExtensions(String... extensions) {
			for(String ext:extensions){
				archiveFileExtension(ext);
			}
	    	return this;
	    }
		/**
		 * Add additional file extension types to denote an archive resources (like a jar). E.g. 'jar'
		 * 
		 * Default contains jar,zip,war,ear,nar
		 * 
		 * @param extension
		 * @return
		 */
		public Builder archiveFileExtension(String extension) {
			this.archiveTypes.add(extension);
	    	return this;
	    }

        public Builder urls(Iterable<URL> urls) {
            for(URL url:urls){
                if(url.getProtocol().equals("file")){
                    root(new File(url.getPath()));
                }
            }
            return this;
        }
        
        
		public Builder root(String path) {
	    	root(new File(path));
	    	return this;
	    }
	
		public Builder roots(Collection<File> paths) {
			for(File path:paths){
				root(path);
			}
	    	return this;
	    }
		
		public Builder rootsPaths(Collection<String> paths, RootType relation, RootContentType contentType) {
            for (String path : paths) {
                Root r = toRootOrNull(new File(path), relation, contentType);
                if (r != null) {
                    root(r);
                }
            }
            return this;
        }
		
        public Builder roots(Collection<File> paths, RootType relation, RootContentType contentType) {
            for (File path : paths) {
                Root r = toRootOrNull(path, relation, contentType);
                if (r != null) {
                    root(r);
                }
            }
            return this;
        }
		
        private static Root toRootOrNull(File f, RootType relation, RootContentType contentType) {
            if (DirectoryRoot.is(f)) {
                return new DirectoryRoot(f, relation, contentType);
            }
            if (ArchiveRoot.is(f)) {
                return new ArchiveRoot(f, relation, contentType);
            }
            return null;
        }
		
        public Builder root(File path){
        	root(path,false);
        	return this;
        }
        
        /**
         * If true silently ignore roots which are not understood (by extension). E.g. a root like /path/to/file.xyz
         * 
         * @return this
         */
        public Builder ignoreUnknownRootTypes(boolean ignore){
        	this.ignoreUnknownRootTypes = ignore;
        	return this;
        }
        
		public Builder root(File path, boolean skipUnknown) {
			if(path.isFile()){
				String extension = Files.getFileExtension(path.getName()).toLowerCase();
				if(archiveTypes.contains(extension)){
					root(new ArchiveRoot(path,RootType.DEPENDENCY, RootContentType.BINARY));	
				} else {
					if(!ignoreUnknownRootTypes && !ignoreTypes.contains(extension)){
						throw new IllegalArgumentException("Don't currently know how to handle roots with file extension '." + extension + "' (for path '" +path.getAbsolutePath() + "')"); 
					}
				}
			} else {
				root(new DirectoryRoot(path,RootType.DEPENDENCY,RootContentType.BINARY));
			}
			return this;
	    }
	
		public Builder roots(IBuilder<? extends Iterable<Root>> builder) {
            roots(builder.build());
            return this;
        }
		
		public Builder roots(Iterable<Root> roots) {
			for(Root root:roots){
				root(root);
			}
			return this;
		}
		
		public Builder root(IBuilder<Root> builder) {
		    root(builder.build());
		    return this;
		}
		
        public Builder root(Root root) {
            String path = root.getFullPath();
            if (!roots.containsKey(path) || (root.getType() != RootType.UNKNOWN)) { //always replace UNKNOWN with a  known type
                roots.put(path, root);
            }
            return this;
        }
		
		public Builder all() {
			mainSrcDir(true);
			testSrcDir(true);
			
			generatedSrcDir(true);
			testGeneratedSrcDir(true);

			mainCompiledDir(true);
			testCompiledDir(true);

			classpath(true);
			return this;
		}

		public Builder srcDirsOnly() {
			mainSrcDir(true);
			generatedSrcDir(true);
			testSrcDir(true);
			testGeneratedSrcDir(true);
			classpath(false);
			return this;
		}
		
		public Builder mainSrcDir(boolean b) {
			this.includeMainSrcDir = b;
			return this;
		}
	
		public Builder testSrcDir(boolean b) {
			this.includeTestSrcDir = b;
			return this;
		}
	
		public Builder generatedSrcDir(boolean b) {
			this.includeGeneratedSrcDir = b;
			return this;
		}
	
		public Builder mainCompiledDir(boolean b) {
			this.includeMainCompiledDir = b;
			return this;
		}
		
		public Builder testCompiledDir(boolean b) {
			this.includeTestCompiledDir = b;
			return this;
		}
	
		  public Builder testGeneratedSrcDir(boolean b) {
	            this.includeTestGeneratedSrcDir = b;
	            return this;
	        }
	    
		  
		public Builder classpath(boolean b) {
	    	this.includeClasspath = b;
	    	return this;
	    }
	}
}