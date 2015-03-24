package org.codemucker.jfind;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newLinkedHashMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.codemucker.jfind.Root.RootType;
import org.codemucker.lang.IBuilder;
import org.codemucker.lang.PathUtil;

import com.google.common.base.Function;

public class ClassScanner {

    public static interface Filter {
        public boolean isInclude(Object obj);

        public boolean isIncludeRoot(Root root);

        public boolean isIncludeResource(RootResource resource);

        public boolean isIncludeClassResource(ClassResource resource);

        public boolean isIncludeClass(Class<?> classToMatch);

        public boolean isIncludeArchive(RootResource archiveFile);
    }

    private static final Logger log = LogManager.getLogger(ClassScanner.class);


    private static final MatchListener<Object> DEFAULT_LISTENER = new MatchListener<Object>() {
        @Override
        public void onMatched(Object obj) {
        }
        
        @Override
        public void onIgnored(Object obj) {
        }

        @Override
        public void onError(Object record, Throwable t) throws Exception {
            throw new JFindException(String.format("Error processing '%s'", record),t);
            //log.warn(String.format("Error processing '%s'", record),e);
            
        }
    };

    private final List<Root> scanRoots;
    private final ClassLoader classLoader;
    private final Filter filter;
    private final MatchListener<Object> listener;
    
    public static Builder with() {
        return new Builder();
    }

    private ClassScanner(Iterable<Root> roots, Filter filter, ClassLoader classLoader, MatchListener<Object> listener) {
        this.scanRoots = ensureUnique(roots);
        this.filter = checkNotNull(filter, "expect filter");
        this.classLoader = checkNotNull(classLoader, "expect class loader");
        this.listener = listener==null?DEFAULT_LISTENER:listener;
    }

    private List<Root> ensureUnique(Iterable<Root> roots) {
        checkNotNull(roots, "expect class path roots to search");
        Map<String, Root> map = newLinkedHashMap();
        for (Root root : roots) {
            String key = root.getFullPath();
            if ((RootType.UNKNOWN != root.getType()) || !map.containsKey(key)) {
                map.put(key, root);
            }
        }
        return newArrayList(map.values());
    }

    public FindResult<ReflectedClass> findReflectedClasses() {
        return findClasses().transform(new Function<Class<?>, ReflectedClass>() {

            @Override
            public ReflectedClass apply(Class<?> input) {
                return ReflectedClass.from(input);
            }
        });
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
            if (filter.isInclude(loadedClass) && filter.isIncludeClass(loadedClass)) {
                listener.onMatched(loadedClass);
                foundClasses.add(loadedClass);
            } else {
                listener.onIgnored(loadedClass);
            }
        } catch (Throwable e) {
            // allow clients to ignore errors if they want
            try {
                listener.onError(resource, e);
            } catch(RuntimeException rethrown){
                throw rethrown;
            } catch (Throwable rethrown) {
                throw new JFindException("error loading class", rethrown);
            }
        }
    }

    private Class<?> loadClass(String className) {
        try {
            return classLoader.loadClass(className);
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
        Collection<ClassResource> found = newArrayList();
        for (RootResource resource : resources) {
            walkClassNames(found, resource);
        }
        return found;
    }

    private void walkClassNames(Collection<ClassResource> found, RootResource resource) {
        if (resource.hasExtension("class")) {
            String className = PathUtil.filePathToClassNameOrNull(resource.getRelPath());
            if(className != null){
            	ClassResource classResource = new ClassResource(resource, className);
            	 if (filter.isInclude(classResource) && filter.isIncludeClassResource(classResource)) {
                     listener.onMatched(classResource);
                     found.add(classResource);
                 } else {
                	 listener.onIgnored(classResource);
                 }
            }
        }
    }

    public FindResult<RootResource> findResources() {
        return DefaultFindResult.from(findResources(scanRoots));
    }

    private Collection<RootResource> findResources(Iterable<Root> roots) {
        final Collection<RootResource> resources = newArrayList();
        RootVisitor visitor = new BaseRootVisitor() {
            @Override
            public boolean visit(Root root) {
                if (filter.isInclude(root) && filter.isIncludeRoot(root)) {
                    listener.onMatched(root);
                    return true;
                } else {
                    listener.onIgnored(root);
                    return false;
                }
            }

            @Override
            public boolean visit(RootResource resource) {
                if (filter.isInclude(resource) && filter.isIncludeResource(resource)) {
                    listener.onMatched(resource);
                    resources.add(resource);
                } else {
                    listener.onIgnored(resource);
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

        private Filter filter;
        private ClassLoader classLoader;
        private List<Root> scanRoots = newArrayList();
        private MatchListener<Object> listener;
        
        public ClassScanner build() {
            return new ClassScanner(scanRoots, toFilter(), toClassLoader(), listener);
        }

        private ClassLoader toClassLoader() {
            if (classLoader != null) {
                return classLoader;
            }
            // TODO:return a reloading classloader (so we can dump loaded
            // classes found during search)
            return Thread.currentThread().getContextClassLoader();
        }

        private Filter toFilter() {
            return filter != null ? filter : new BaseFilter();
        }

        public Builder scanRoots(IBuilder<? extends Iterable<? extends Root>> builder) {
            scanRoots(builder.build());
            return this;
        }

        public Builder scanRoots(Iterable<? extends Root> roots) {
            scanRoots = newArrayList(roots);
            return this;
        }

        public Builder filter(IBuilder<? extends Filter> builder) {
            filter(builder.build());
            return this;
        }

        public Builder filter(Filter filter) {
            this.filter = filter;
            return this;
        }

        public Builder listener(MatchListener<Object> listener) {
            this.listener = listener;
            return this;
        }

        public Builder classLoader(ClassLoader classLoader) {
            this.classLoader = classLoader;
            return this;
        }
    }
}