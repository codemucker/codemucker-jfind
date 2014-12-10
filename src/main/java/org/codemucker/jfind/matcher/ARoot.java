package org.codemucker.jfind.matcher;

import java.util.regex.Pattern;

import org.codemucker.jfind.Root;
import org.codemucker.jfind.Root.RootContentType;
import org.codemucker.jfind.Root.RootType;
import org.codemucker.jmatch.AString;
import org.codemucker.jmatch.AbstractMatcher;
import org.codemucker.jmatch.AbstractNotNullMatcher;
import org.codemucker.jmatch.Description;
import org.codemucker.jmatch.Logical;
import org.codemucker.jmatch.MatchDiagnostics;
import org.codemucker.jmatch.Matcher;
import org.codemucker.jmatch.PropertyMatcher;
import org.codemucker.jmatch.expression.AbstractMatchBuilderCallback;
import org.codemucker.jmatch.expression.ExpressionParser;
import org.codemucker.jpattern.Dependency;

public class ARoot extends PropertyMatcher<Root> {

    public static ARoot that() {
        return with();
    }

    public static ARoot with() {
        return new ARoot();
    }

    private ARoot() {
        super(Root.class);
    }

    public static Matcher<Root> any() {
        return Logical.any();
    }

    public static Matcher<Root> none() {
        return Logical.none();
    }

    public ARoot extension(String extension) {
        pathMatchingAntPattern("**." + extension);
        return this;
    }

    public ARoot nameMatchingAntPattern(String antPattern) {
        pathMatchingAntPattern("**/" + antPattern);
        return this;
    }

    public ARoot pathMatchingAntPattern(String antPattern) {
        path(AString.matchingAntPattern(antPattern));
        return this;
    }

    public ARoot pathMatchingRegex(Pattern pattern) {
        path(AString.matchingRegex(pattern));
        return this;
    }

    public ARoot dependenciesExpression(String expression) {
    	if(expression != null && expression.trim().length() != 0){
    		Matcher<Root> matcher = ExpressionParser.parse(expression, new RootMatchBuilderCallback());
    		if(matcher instanceof ARoot){ //make the matching are bit faster by directly running the matchers directly
    			for(Matcher<Root> m:((ARoot)matcher).getMatchers()){
    				addMatcher(m);
    			}
    		} else {
    			addMatcher(matcher);
    		}
    	}
        return this;
    }
    
    public ARoot dependencies(Dependency... dependencies) {
    	for (Dependency dep : dependencies) {
            dependency(dep.group(), dep.artifact(), dep.classifier(), dep.extension());
        }
        return this;
    }
    
    public ARoot dependency(String mavenDependency) {
    	String[] parts = mavenDependency.split(":");
    	String group = get(0,parts);
    	String artifactId = get(1,parts);
    	String classifier = get(2,parts);
    	String extension = get(3,parts);
    	
    	dependency(group, artifactId, classifier, extension);
    	return this;
    }
    
    private static String get(int pos,String[] parts){
    	if(pos >= parts.length){
    		return null;
    	}
    	String s = parts[pos];
    	if(s==null){
    		return null;
    	}
    	s=s.trim();
    	return s.length()==0?null:s;
    }
    
    public ARoot dependency(String group, String artifactId, String classifier, String extension) {
        //e.g. //projectlombok\lombok\1.14.8\lombok-1.14.8.jar
        String groupPath = trimTo(group,"*").replace('.', '/');
        artifactId = trimTo(artifactId,"*");
        classifier = trimTo(classifier,"*");
        extension = trimTo(extension, "jar|zip|war|ear");
        
        final Matcher<String> dependencyPathMatcher = AString.matchingAntFilePathPattern("**" +groupPath + "/" + artifactId + "/*/" + artifactId + "-" + classifier + "." + extension);
        
        addMatcher(new AbstractNotNullMatcher<Root>() {

            @Override
            protected boolean matchesSafely(Root actual, MatchDiagnostics diag) {
                if (actual.getType() == RootType.DEPENDENCY && actual.isArchive()) {
                    return diag.tryMatch(this, actual.getPathName(), dependencyPathMatcher);
                }
                return true;
            }

            @Override
            public void describeTo(Description desc) {
                // super.describeTo(desc);
                // desc.text("not null resouce");
                desc.value("of type " + RootType.DEPENDENCY + " and path", dependencyPathMatcher);
            }
        });
        return this;
    }
    
    private static String trimTo(String val,String defaultVal){
    	if(val==null){
    		return defaultVal;
    	}
    	val = val.trim();
    	return val.length()==0?defaultVal:val;
    }
    
    public ARoot path(String path) {
        path(AString.equalTo(path));
        return this;
    }

    public ARoot path(final Matcher<String> pathMatcher) {
        addMatcher(new AbstractNotNullMatcher<Root>() {

            @Override
            protected boolean matchesSafely(Root actual, MatchDiagnostics diag) {
                return diag.tryMatch(this, actual.getPathName(), pathMatcher);
            }

            @Override
            public void describeTo(Description desc) {
                // super.describeTo(desc);
                // desc.text("not null resouce");
                desc.value("path", pathMatcher);
            }
        });
        return this;
    }

    public ARoot isArchive(){
        isArchive(true);
        return this;
    }
    
    public ARoot isArchive(final boolean isArchive) {
        addMatcher(new AbstractNotNullMatcher<Root>() {

            @Override
            protected boolean matchesSafely(Root actual, MatchDiagnostics diag) {
                return isArchive == actual.isArchive();
            }

            @Override
            public void describeTo(Description desc) {
                desc.text("is" + (isArchive?"":" not") + " an archive");
            }
        });
        return this;
    }
    
    public ARoot isDirectory() {
        isDirectory(true);
        return this;
    }
    
    public ARoot isDirectory(final boolean isDirectory) {
        addMatcher(new AbstractNotNullMatcher<Root>() {

            @Override
            protected boolean matchesSafely(Root actual, MatchDiagnostics diag) {
                return isDirectory == actual.isDirectory();
            }

            @Override
            public void describeTo(Description desc) {
                desc.text("path that is" + (isDirectory?"":" not") + " a directory");
            }
        });
        return this;
    }
    
    public ARoot isSrc() {
        isContentType(new AbstractMatcher<RootContentType>() {
            @Override
            protected boolean matchesSafely(RootContentType actual, MatchDiagnostics diag) {
                return RootContentType.SRC.equals(actual) || RootContentType.MIXED.equals(actual);
            }

            @Override
            public void describeTo(Description desc) {
                desc.text(RootContentType.SRC.name() + "|" + RootContentType.MIXED.name());
            }
        });        
        return this;
    }

    public ARoot isContentType(final RootContentType type) {
        isContentType(new AbstractMatcher<RootContentType>() {
            @Override
            protected boolean matchesSafely(RootContentType actual, MatchDiagnostics diag) {
                return type.equals(actual);
            }

            @Override
            public void describeTo(Description desc) {
                desc.text(type.name());
            }
        });
        return this;
    }

    public ARoot isContentType(final Matcher<RootContentType> matcher) {
        addMatcher(new AbstractNotNullMatcher<Root>() {
    
            @Override
            protected boolean matchesSafely(Root actual, MatchDiagnostics diag) {
                return diag.tryMatch(this, actual.getContentType(), matcher);
            }
    
            @Override
            public void describeTo(Description desc) {
                desc.value("content type",matcher);
            }
        });
        return this;
    }

    public ARoot isNotType(final RootType type) {
        isType(new AbstractMatcher<RootType>() {

            @Override
            protected boolean matchesSafely(RootType actual, MatchDiagnostics diag) {
                return !type.equals(actual);
            }

            @Override
            public void describeTo(Description desc) {
                desc.value("not of type ",type);
            }
        });
        return this;
    }
    
    public ARoot isType(final RootType type) {
        isType(new AbstractMatcher<RootType>() {

            @Override
            protected boolean matchesSafely(RootType actual, MatchDiagnostics diag) {
                return type.equals(actual);
            }

            @Override
            public void describeTo(Description desc) {
                desc.value("of type ",type);
            }
        });
        return this;
    }

    public ARoot isType(final Matcher<RootType> matcher) {
        addMatcher(new AbstractNotNullMatcher<Root>() {

            @Override
            protected boolean matchesSafely(Root actual, MatchDiagnostics diag) {
                return diag.tryMatch(this, actual.getType(),matcher);
            }

            @Override
            public void describeTo(Description desc) {
                desc.value("path:",matcher);
            }
        });
        return this;
    }

    private static class RootMatchBuilderCallback extends AbstractMatchBuilderCallback<Root>{

		@Override
		protected Matcher<Root> newMatcher(String expression) {
			return ARoot.with().dependency(expression);
		}
    }
    
}
