
package org.codemucker.jfind;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents the top level node to resolve relative resource paths from. This could be 
 * a directory, zip file, network resource or other. In most cases this is likely to be
 * the root of a source directory or a jar file.
 *
 */
public interface Root {

	/**
	 * Provide some default categorisation of the root type so that tooling can decide where to 
	 * place newly generated resources, or decide if a root is to be used for a given type of processing
	 * or searching
	 */
	public static enum RootType {
    	MAIN
    	, TEST
    	, DEPENDENCY
    	, GENERATED
    	, UNKNOWN;
    }
	
	public static enum RootContentType {
    	SRC, BINARY, MIXED;
    }

    /**
     * Return information about the full path of the given relative resource. 
     * 
     * The returned value does not have to represent a file or url path, it it merely informative for debug and errors messages
     * 
     * @param relPath the relative path of the resource within this root
     * @return the full path info, not machine readable
     */
    String getFullPathInfo(String relPath);
    
	/**
	 * Return a stream to read the given relative stream from
	 * @param relPath
	 * @return
	 * @throws IOException if it was not possible to read from this stream. This could include
	 * the given resource not existing, not having permission, or this root not supporting read
	 * operations.
	 */
	InputStream getResourceInputStream(String relPath) throws IOException;
	
	/**
	 * Return a stream to write to the given resource
	 * @param relPath
	 * @return
	 * @throws IOException if it was not possible to write to the given resource for any reason. This
	 * could include not having permissions, this root not supporting writing.
	 */
	OutputStream getResourceOutputStream(String relPath) throws IOException;

	public boolean canWriteResource(String relPath);
	public boolean canReadReource(String relPath);
	
	String getPathName();

	RootType getType();
	RootContentType getContentType();

	void accept(RootVisitor visitor);

}