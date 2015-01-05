
package org.codemucker.jfind;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

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
	 * 
	 * TODO:rename to RooCategory
	 */
	public static enum RootType {
    	MAIN
    	, TEST
    	, DEPENDENCY
    	, /** The JVM and related jars */ SYSTEM
    	, GENERATED
    	, UNKNOWN;
    }
	
	public static enum RootContentType {
    	SRC, BINARY, MIXED;
    }
	
	public static final long TIMESTAMP_NOT_EXIST = -1;

    /**
     * Return information about the full path of the given relative resource. 
     * 
     * The returned value does not have to represent a file or url path if for example this is backed onto a database or archive
     * 
     * @param relPath the relative path of the resource within this root
     * @return the full path info which may or may not represent a file path
     */
    String getResourceFullPath(String relPath);
    URL getResourceUrl(String relPath);
    long getResourceLastModified(String relPath);
    
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
	
	/**
	 * Check if the given resource exists and is readable
	 * @param relPath
	 * @return
	 */
	public boolean canReadResource(String relPath);

	String getFullPath();
    URL toURL();
	RootType getType();
	RootContentType getContentType();
	RootResource getResource(String relPath);
    
	void accept(RootVisitor visitor);

	public boolean isArchive();
	public boolean isDirectory();
	
    
}
