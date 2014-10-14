package org.codemucker.jfind.matcher;

import java.util.regex.Pattern;

import org.codemucker.jfind.Root;
import org.codemucker.jfind.Root.RootType;
import org.codemucker.jmatch.AString;
import org.codemucker.jmatch.AbstractNotNullMatcher;
import org.codemucker.jmatch.Description;
import org.codemucker.jmatch.Logical;
import org.codemucker.jmatch.MatchDiagnostics;
import org.codemucker.jmatch.Matcher;
import org.codemucker.jmatch.PropertyMatcher;

public class ARoot extends PropertyMatcher<Root> {

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

    public ARoot dependency(String group, String artifactId) {
        //e.g. //projectlombok\lombok\1.14.8\lombok-1.14.8.jar
        String groupPath = group.replace('.', '/');
        final Matcher<String> dependencyPathMatcher = AString.matchingAntPattern("**" +groupPath + "/" + artifactId + "/*/" + artifactId + "-*.*");
        
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
                desc.value("root of type " + RootType.DEPENDENCY + " with path", dependencyPathMatcher);
            }
        });
        return this;
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
                desc.value("root with path", pathMatcher);
            }
        });
        return this;
    }

}
