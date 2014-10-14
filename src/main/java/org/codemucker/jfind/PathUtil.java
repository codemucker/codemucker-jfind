package org.codemucker.jfind;

class PathUtil {
    static String toForwardSlashes(String s) {
        if (s == null) {
            return null;
        }
        return s.replace('\\', '/');
    }
}
