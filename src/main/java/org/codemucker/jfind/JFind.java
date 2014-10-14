package org.codemucker.jfind;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newLinkedHashMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.codemucker.jfind.Root.RootType;
import org.codemucker.jtest.ClassNameUtil;
import org.codemucker.lang.IBuilder;

public class JFind {

    public static interface Filter {
        public boolean isInclude(Object obj);

        public boolean isIncludeRoot(Root root);

        public boolean isIncludeDir(RootResource resource);

        public boolean isIncludeResource(RootResource resource);

        public boolean isIncludeClassname(RootResource resource,String className);

        public boolean isIncludeClass(RootResource resource,Class<?> classToMatch);

        public boolean isIncludeArchive(RootResource archiveFile);
    }

    public static interface MatchedCallback {
        public void onMatched(Object obj);

        public void onRootMatched(Root matchedRoot);

        public void onResourceMatched(RootResource matchedResource);

        public void onArchiveMatched(RootResource matchedArchive);

        public void onClassNameMatched(RootResource resource, String matchedClassName);

        public void onClassMatched(RootResource resource, Class<?> matchedClass);
    }

    public static interface IgnoredCallback {
        public void onIgnored(Object obj);

        public void onRootIgnored(Root ignoredRoot);

        public void onResourceIgnored(RootResource ignoredResource);

        public void onArchiveIgnored(RootResource ignoredArchive);

        public void onClassNameIgnored(RootResource resource, String ignoredClassName);

        public void onClassIgnored(RootResource resource, Class<?> ignoredClass);
    }

    public static interface ErrorCallback {
        public void onError(Object obj, Exception e);

        public void onResourceError(RootResource resource, Exception e);

        public void onArchiveError(RootResource archive, Exception e);

        public void onClassError(RootResource resource, String fullClassname, Exception e);
    }

    private final List<Root> classPathRoots;
    private final ClassLoader classLoader;
    private final Filter filter;
    private final ErrorCallback errorHandler;
    private final IgnoredCallback ignoredCallback;
    private final MatchedCallback matchedCallback;

    public static Builder with() {
        return new Builder();
    }

    /**
     * A different builder api
     */
    public static Criteria withCriteria() {
        return new Criteria();
    }

    private JFind(Iterable<Root> roots, Filter filter, ClassLoader classLoader, ErrorCallback errorHandler, IgnoredCallback ignoredCallback,
            MatchedCallback matchedCallback) {
        this.classPathRoots = ensureUnique(roots);
        this.filter = checkNotNull(filter, "expect filter");
        this.classLoader = checkNotNull(classLoader, "expect class loader");
        this.errorHandler = checkNotNull(errorHandler, "expect errorHandlerr");
        this.ignoredCallback = checkNotNull(ignoredCallback, "expect ignoredCallback");
        this.matchedCallback = checkNotNull(matchedCallback, "expect matchedCallback");
    }

    private List<Root> ensureUnique(Iterable<Root> roots) {
        checkNotNull(roots, "expect class path roots to search");
        Map<String, Root> map = newLinkedHashMap();
        for (Root root : roots) {
            String key = root.getPathName();
            if ((RootType.UNKNOWN != root.getType()) || !map.containsKey(key)) {
                map.put(key, root);
            }
        }
        // checkState(map.size()>0,"need some class path roots to search");
        return newArrayList(map.values());
    }

    public FindResult<Class<?>> findClasses() {
        return DefaultFindResult.from(findClasses(findClassNames()));
    }

    private Collection<Class<?>> findClasses(Iterable<ClassResource> classNames) {
        Collection<Class<?>> classes = newArrayList();
        for (ClassResource r : classNames) {
            loadClass(classes, r.getResource(), r.getClassName());
        }
        return classes;
    }

    private void loadClass(Collection<Class<?>> foundClasses, RootResource resource, String className) {
        Class<?> loadedClass = null;
        try {
            loadedClass = loadClass(className);
            if (filter.isInclude(loadedClass) && filter.isIncludeClass(resource,loadedClass)) {
                matchedCallback.onMatched(loadedClass);
                matchedCallback.onClassMatched(resource, loadedClass);
                foundClasses.add(loadedClass);
            } else {
                ignoredCallback.onIgnored(loadedClass);
                ignoredCallback.onClassIgnored(resource, loadedClass);
            }
        } catch (Exception e) {
            // allow clients to ignore errors if they want
            errorHandler.onClassError(resource, className, e);
        }
    }

    private Class<?> loadClass(String className) {
        try {
            return (Class<?>) classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new JFindException("couldn't load class " + className, e);
        } catch (NoClassDefFoundError e) {
            throw new JFindException("couldn't load class " + className, e);
        } catch (IllegalAccessError e) {
            throw new JFindException("couldn't load class " + className, e);
        }
    }

    public FindResult<ClassResource> findClassNames() {
        return DefaultFindResult.from(findClassNames(findResources()));
    }

    private Collection<ClassResource> findClassNames(Iterable<RootResource> resources) {
        Collection<ClassResource> foundClassNames = newArrayList();
        for (RootResource resource : resources) {
            walkClassNames(foundClassNames, resource);
        }
        return foundClassNames;
    }

    private void walkClassNames(Collection<ClassResource> foundClassNames, RootResource resource) {
        if (resource.hasExtension("class")) {
            String className = ClassNameUtil.pathToClassName(resource.getRelPath());
            if (filter.isInclude(className) && filter.isIncludeClassname(resource,className)) {
                matchedCallback.onMatched(className);
                matchedCallback.onClassNameMatched(resource, className);
                foundClassNames.add(new ClassResource(resource, className));
            } else {
                ignoredCallback.onIgnored(className);
                ignoredCallback.onClassNameIgnored(resource, className);
            }
        }
    }

    public FindResult<RootResource> findResources() {
        return DefaultFindResult.from(findResources(classPathRoots));
    }

    private Collection<RootResource> findResources(Iterable<Root> roots) {
        final Collection<RootResource> resources = newArrayList();
        RootVisitor visitor = new BaseRootVisitor() {
            @Override
            public boolean visit(Root root) {
                if (filter.isInclude(root) && filter.isIncludeRoot(root)) {
                    matchedCallback.onMatched(root);
                    matchedCallback.onRootMatched(root);
                    return true;
                } else {
                    ignoredCallback.onIgnored(root);
                    ignoredCallback.onRootIgnored(root);
                    return false;
                }
            }

            @Override
            public boolean visit(RootResource resource) {
                if (filter.isInclude(resource) && filter.isIncludeResource(resource)) {
                    matchedCallback.onMatched(resource);
                    matchedCallback.onResourceMatched(resource);
                    resources.add(resource);
                } else {
                    ignoredCallback.onIgnored(resource);
                    ignoredCallback.onResourceIgnored(resource);
                }
                return true;
            }
        };

        for (Root root : roots) {
            root.accept(visitor);
        }
        return resources;
    }

    public static class Builder {

        private MatchedCallback findMatchedCallback;
        private IgnoredCallback findIgnoredCallback;
        private ErrorCallback findErrorCallback;
        private Filter finderFilter;
        private ClassLoader classLoader;
        private List<Root> classPathRoots = newArrayList();

        public JFind build() {
            return new JFind(classPathRoots, toFilter(), toClassLoader(), toErrorCallback(), toIgnoredCallback(), toMatchedCallback());
        }

        private ClassLoader toClassLoader() {
            if (classLoader != null) {
                return classLoader;
            }
            // TODO:return a reloading classloader (so we can dump loaded
            // classes found during search)
            return Thread.currentThread().getContextClassLoader();
        }

        private IgnoredCallback toIgnoredCallback() {
            return findIgnoredCallback != null ? findIgnoredCallback : new BaseIgnoredCallback();
        }

        private MatchedCallback toMatchedCallback() {
            return findMatchedCallback != null ? findMatchedCallback : new BaseMatchedCallback();
        }

        private ErrorCallback toErrorCallback() {
            return findErrorCallback != null ? findErrorCallback : new LoggingErrorCallback();
        }

        private Filter toFilter() {
            return finderFilter != null ? finderFilter : new BaseFilter();
        }

        public Builder roots(IBuilder<Iterable<Root>> builder) {
            roots(builder.build());
            return this;
        }

        public Builder roots(Iterable<Root> roots) {
            classPathRoots.addAll(newArrayList(roots));
            return this;
        }

        public Builder matchedCallback(MatchedCallback callback) {
            this.findMatchedCallback = callback;
            return this;
        }

        public Builder ignoredCallback(IgnoredCallback callback) {
            this.findIgnoredCallback = callback;
            return this;
        }

        public Builder errorCallback(ErrorCallback callback) {
            this.findErrorCallback = callback;
            return this;
        }

        public Builder filter(IBuilder<? extends Filter> builder) {
            filter(builder.build());
            return this;
        }

        public Builder filter(Filter filter) {
            this.finderFilter = filter;
            return this;
        }

        public Builder classLoader(ClassLoader classLoader) {
            this.classLoader = classLoader;
            return this;
        }

        public Builder swallowErrors() {
            this.findErrorCallback = new BaseErrorCallback();
            return this;
        }
    }
}