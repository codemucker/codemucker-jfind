package com.bertvanbrakel.test.finder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Pattern;

public class ClassFinderOptions {
	
	private final Collection<String> projectFiles = Arrays.asList(
		"pom.xml", // maven2
        "project.xml", // maven1
        "build.xml", // ant
        ".project", // eclipse
        ".classpath" // eclipse
	);
	
	public static final ClassMatcher MATCHER_ANONYMOUS = new ClassMatcher() {
		@Override
		public boolean matchClass(Class found) {
			return found.isAnonymousClass();
		}
	};
	
	public static final ClassMatcher MATCHER_ENUM = new ClassMatcher() {
		@Override
		public boolean matchClass(Class found) {
			return found.isEnum();
		}
	};
	
	public static final ClassMatcher MATCHER_INNER_CLASS = new ClassMatcher() {
		@Override
		public boolean matchClass(Class found) {
			return found.isMemberClass();
		}
	};

	public static final ClassMatcher MATCHER_INTERFACE = new ClassMatcher() {
		@Override
		public boolean matchClass(Class found) {
			return found.isInterface();
		}
	};

	private final Collection<File> classPathsDir = new HashSet<File>();
	private boolean includeClassesDir = true;
	private boolean includeTestDir = false;

	private final Collection<FileMatcher> excludeFileNameMatchers = new ArrayList<FileMatcher>();
	private final Collection<FileMatcher> includeFileNameMatchers = new ArrayList<FileMatcher>();
	
	private final Collection<ClassMatcher> includeClassMatchers = new ArrayList<ClassMatcher>();
	private final Collection<ClassMatcher> excludeClassMatchers = new ArrayList<ClassMatcher>();
	
	private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	
	public Collection<String> getProjectFiles() {
    	return projectFiles;
    }

	public ClassLoader getClassLoader() {
    	return classLoader;
    }

	public ClassFinderOptions classLoader(ClassLoader classLoader) {
    	this.classLoader = classLoader;
    	return this;
    }

	public ClassFinderOptions includeClassesDir(boolean b) {
		this.includeClassesDir = b;
		return this;
	}

	public ClassFinderOptions includeTestDir(boolean b) {
		this.includeTestDir = b;
		return this;
	}

	public Collection<File> getClassPathsDir() {
		return classPathsDir;
	}

	public boolean isIncludeClassesDir() {
		return includeClassesDir;
	}

	public boolean isIncludeTestDir() {
		return includeTestDir;
	}

	public ClassFinderOptions addClassPath(File dir) {
		classPathsDir.add(dir);
		return this;
	}

	public ClassFinderOptions excludeFileName(String path) {
		String regExp = antToRegExp(path);
		excludeFileName(Pattern.compile(regExp));
		return this;
	}
	
	public ClassFinderOptions excludeFileName(Pattern pattern) {
		excludeFileName(new RegExpPatternFileNameMatcher(pattern));
		return this;
	}

	public ClassFinderOptions excludeFileName(FileMatcher matcher) {
		this.excludeFileNameMatchers.add(matcher);
		return this;
	}

	public ClassFinderOptions includeFileName(String pattern) {
		String regExp = antToRegExp(pattern);
		includeFileName(Pattern.compile(regExp));
		return this;
	}

	public ClassFinderOptions includeFileName(Pattern pattern) {
		includeFileName(new RegExpPatternFileNameMatcher(pattern));
		return this;
	}
	
	public ClassFinderOptions includeFileName(FileMatcher matcher) {
		this.includeFileNameMatchers.add(matcher);
		return this;
	}
	
	private String antToRegExp(String antPattern) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < antPattern.length(); i++) {
			char c = antPattern.charAt(i);
			if (c == '.') {
				sb.append("\\.");
			} else if (c == '*') {
				sb.append(".*");
			} else if (c == '?') {
				sb.append(".?");
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	public ClassFinderOptions assignableTo(Class<?>... superclass) {
		includeClassMatching(new ClassImplementsMatcher(superclass));
		return this;
	}
	
	public ClassFinderOptions includeClassMatching(ClassMatcher matcher) {
		this.includeClassMatchers.add(matcher);
		return this;
	}
	
	public ClassFinderOptions excludeEnum() {
		excludeClassMatching(MATCHER_ENUM);
		return this;
	}

	public ClassFinderOptions excludeAnonymous() {
		excludeClassMatching(MATCHER_ANONYMOUS);
		return this;
	}

	public ClassFinderOptions excludeInner() {
		excludeClassMatching(MATCHER_INNER_CLASS);
		return this;
	}

	public ClassFinderOptions excludeInterfaces() {
		excludeClassMatching(MATCHER_INTERFACE);
		return this;
	}

	public ClassFinderOptions excludeClassMatching(ClassMatcher matcher) {
		this.excludeClassMatchers.add(matcher);
		return this;
	}
	
	public FileMatcher toFileMatcher() {
		return new FileMatcher() {
			@Override
			public boolean matchFile(File file, String relPath) {
				boolean include = true;
				if (includeFileNameMatchers != null && includeFileNameMatchers.size() > 0) {
					include = false;// by default if we have includes we exclude
									// all except matches
					for (FileMatcher matcher : includeFileNameMatchers) {
						if (matcher.matchFile(null, relPath)) {
							include = true;
							break;
						}
					}
				}
				if (include && (excludeFileNameMatchers != null && excludeFileNameMatchers.size() > 0)) {
					for (FileMatcher matcher : excludeFileNameMatchers) {
						if (matcher.matchFile(null, relPath)) {
							include = false;
						}
					}
				}
				return include;
			}
		};
	}
	
	public ClassMatcher toClassMatcher() {
		return new ClassMatcher() {
			@Override
			public boolean matchClass(Class klass) {
				boolean include = true;
				if (includeClassMatchers != null && includeClassMatchers.size() > 0) {
					include = false;
					for (ClassMatcher matcher : includeClassMatchers) {
						if (matcher.matchClass(klass)) {
							include = true;
							break;
						}
					}
				}
				if (excludeClassMatchers != null && excludeClassMatchers.size() > 0) {
					for (ClassMatcher matcher : excludeClassMatchers) {
						if (matcher.matchClass(klass)) {
							include = false;
							break;
						}
					}
				}
				return include;
			}
		};
	}

	protected static class RegExpPatternFileNameMatcher implements FileMatcher {
		private final Pattern pattern;
		
		RegExpPatternFileNameMatcher(Pattern pattern) {
			this.pattern = pattern;
		}

		@Override
		public boolean matchFile(File file, String path) {
			return pattern.matcher(path).matches();
		}
	}
	
	protected static class ClassImplementsMatcher implements ClassMatcher {
		private final Class<?>[] superclass;

		public ClassImplementsMatcher(Class<?>... superclass) {
	        super();
	        this.superclass = superclass;
        }

		@Override
		public boolean matchClass(Class found) {
			for (Class<?> require : superclass) {
				if (!require.isAssignableFrom(found)) {
					return false;
				}
			}
			return true;
		}
	}
}