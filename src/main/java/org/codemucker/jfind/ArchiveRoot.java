package org.codemucker.jfind;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

import com.google.common.base.Objects;

/**
 * Classpath root which handles archive (zip) files
 */
public class ArchiveRoot implements Root {
	private final File archivePath;
	private final RootType type;
	private final RootContentType contentType;
	private final AtomicReference<ZipWrapper> cachedZip = new AtomicReference<ZipWrapper>();
	private static final String[] EXTENSIONS = new String[]{ ".jar",".zip"};
	
	public static boolean is(File f){
	    if(f.isFile()){
	        for(String ext:EXTENSIONS){
	            if(f.getPath().endsWith(ext)){
	                return true;
	            }
	        }
	    }
        return false;
    }
	public ArchiveRoot(File path){
		this(path,RootType.UNKNOWN,RootContentType.BINARY);
	}
	
	public ArchiveRoot(File path,RootType type,RootContentType contentType){
		this.archivePath = checkNotNull(path,"expect path");
		this.type = checkNotNull(type,"expect root relation");
		this.contentType = checkNotNull(contentType,"expect root content type");
		checkState(path.isFile(),"expect archive file to be a file");
	}
	
	@Override
    public boolean canWriteResource(String relPath) {
	    return false;
    }

	@Override
    public boolean canReadResource(String relPath) {
	    return archivePath.exists() && archivePath.canRead() && getZip().hasEntry(relPath);
    }
	

    @Override
    public String getFullPathInfo(String relPath) {
        return archivePath.getAbsolutePath() + "!" + relPath;
    }
    
    @Override
    public RootResource getResource(String relPath){   
        return new RootResource(this, relPath);
    }
    
    @Override
    public URL getUrl(String relPath){
        try {
            return new URL(archivePath.toURI().toURL().toExternalForm() + "!"  + relPath);
        } catch (MalformedURLException e) {
            throw new JFindException("couldn't convert relative path '" + relPath + "' to url", e);
        }
    }
    
    @Override
    public long getLastModified(String relPath) {
        ZipWrapper zip = getZip();
        long ts = zip.getEntry(relPath).getTime();
        if (ts < 0) {
            ts = zip.latModified();
        }
        if (ts <= 0L) {
            ts = Root.TIMESTAMP_NOT_EXIST;
        }
        return ts;
    }
    
    @Override
    public URL toURL(){
        try {
            return archivePath.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new JFindException("couldn't convert archive path '" + archivePath + "' to url", e);
        }
    }
    
	@Override
	public OutputStream getResourceOutputStream(String relPath) throws IOException {
		throw new IOException("Can't don't support writing a resources to an archive");
	}
	
	@Override
	public InputStream getResourceInputStream(String relPath) throws IOException {
		return getZip().getEntryInputStream(relPath);
	}
	
	@Override
	public String getPathName(){
		return PathUtil.toForwardSlashes(archivePath.getAbsolutePath());
	}

	@Override
	public RootType getType(){
		return type;
	}

	@Override
	public RootContentType getContentType(){
		return contentType;
	}
	
	@Override
	public String toString(){
		return Objects
    		.toStringHelper(this)
    		.add("path", getPathName())
    		.add("type", type)
    		.add("contentType", contentType)
    		.add("isArchive", true)
    		.add("exists", archivePath.canRead())
     		.toString();
    }
	
	@Override
	public void accept(RootVisitor visitor) {
		if( visitor.visit(this)){
			visitResources(visitor);
		}
		visitor.endVisit(this);
	}

	private void visitResources(RootVisitor visitor) {
		ZipWrapper zip = getZip();
		try {
			zip.visitZipEntries(visitor);
		} finally {
		    cachedZip.set(null);
		    IOUtils.closeQuietly(zip);
		}
	}
	
   private ZipWrapper getZip() {       
        ZipWrapper wrapper = cachedZip.get();
        if (wrapper == null) {
            wrapper = new ZipWrapper(this, archivePath);
            cachedZip.set(wrapper);
        }
        return wrapper;
    }

	private static boolean isCancelled(){
		return Thread.interrupted();
	}

	private static String ensureStartsWithSlash(String name) {
	    if( !name.startsWith("/")){
	    	name = "/" + name;
	    }
	    return name;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((contentType == null) ? 0 : contentType.hashCode());
		result = prime * result + ((archivePath == null) ? 0 : archivePath.hashCode());
		result = prime * result
				+ ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArchiveRoot other = (ArchiveRoot) obj;
		if (contentType != other.contentType)
			return false;
		if (archivePath == null) {
			if (other.archivePath != null)
				return false;
		} else if (!archivePath.equals(other.archivePath))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

    @Override
    public boolean isArchive() {
        return true;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }
    
    private static class ZipWrapper implements Closeable {
        private final ZipFile zip;
        private final File archivePath;
        private final ArchiveRoot root;
        
        ZipWrapper(ArchiveRoot root, File archivePath) {
            if (!archivePath.exists()) {
                throw new JFindException(String.format("Couldn't find archive %s", root));
            }
            if (!archivePath.canRead()) {
                throw new JFindException(String.format("Couldn't read archive %s as is not readable", root));
            }
            this.archivePath = archivePath;
            this.root = root;
            try {
                zip = new ZipFile(archivePath);
            } catch (ZipException e) {
                throw new JFindException("Error opening archive " + root, e);
            } catch (IOException e) {
                throw new JFindException("Error opening archive " + root, e);
            }
        }
        
        long latModified(){
            return archivePath.lastModified();
        }

        InputStream getEntryInputStream(String relPath) throws IOException {
            return zip.getInputStream(getEntry(relPath));
        }

        boolean hasEntry(String relPath) {
            return zip.getEntry(relPath)!=null;
        }
        
        ZipEntry getEntry(String relPath) {
            relPath = toZipPath(relPath);
            ZipEntry entry = zip.getEntry(relPath);
            if (entry == null) {
                throw new JFindException(String.format("Couldn't find archive entry '%s' in archive %s", relPath,root));
            }
            return entry;
        }
        
        private static String toZipPath(String relPath){
            relPath = relPath.replace('\\', '/');
            if(relPath.startsWith("/")){
                relPath = relPath.substring(1);
            }
            return relPath;
        }

        void visitZipEntries(RootVisitor visitor){
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                if( !entry.isDirectory()){
                    String name = entry.getName();
                    name = ensureStartsWithSlash(name);
                    RootResource zipResourceEntry = new RootResource(root, name);
                    visitor.visit(zipResourceEntry);
                    visitor.endVisit(zipResourceEntry);
                    if (isCancelled()) {
                        return;
                    }
                }
            }
        }
        
        @Override
        public void close() throws IOException {
            IOUtils.closeQuietly(zip);
        }
    }

}
