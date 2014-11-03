package org.codemucker.jfind;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.codemucker.lang.IBuilder;
import org.codemucker.lang.PathUtil;

import com.google.common.base.Objects;

/**
 * Classpath root which handles directory type classpath entries
 */
public class DirectoryRoot implements Root {
	
	private static FileFilter DIR_FILTER = new FileFilter() {
		private static final char HIDDEN_DIR_PREFIX = '.';//like .git, .svn,....
		
		@Override
		public boolean accept(File dir) {
			return dir.isDirectory() && dir.getName().charAt(0) != HIDDEN_DIR_PREFIX && !dir.getName().equals("CVS");
		}
	};
	
	private static FileFilter FILE_FILTER = new FileFilter() {
		@Override
		public boolean accept(File f) {
			return f.isFile();
		}
	};
	
	private final File baseDir;
	private final RootType type;
	private final RootContentType contentType;
	
	public static Builder with(){
	    return new Builder();
	}
	
	public static boolean is(File f){
	    return f.isDirectory();
	}
	
	public DirectoryRoot(File path){
		this(path,RootType.UNKNOWN,RootContentType.BINARY);
	}
	
	public DirectoryRoot(File path,RootType type,RootContentType contentType){
		this.baseDir = checkNotNull(path,"expect path");
		this.type = checkNotNull(type,"expect type (e.g. MAIN,TEST,GENERATED...)");
		this.contentType = checkNotNull(contentType,"expect content type (e.g. SRC,BINARY, MIXED...)");
		if(path.exists() && !path.isDirectory()){
			throw new IllegalArgumentException("expect path to be a directory, path=" + path.getAbsolutePath());
		}
	}
	
	@Override
    public boolean canWriteResource(String relPath) {
	    return baseDir.canWrite();
    }

	@Override
	public boolean canReadResource(String relPath) {
		if (baseDir.canRead()) {
			File f = getByRelPath(relPath);
			return f.exists() && f.canRead();
		}
		return false;
	}

	@Override
	public OutputStream getResourceOutputStream(String relPath) throws IOException {
		if(isDirectoryAndExists()){
			//TODO:check relPath is in the given directory, no escaping up!
			File f = new File(baseDir.getAbsolutePath(),relPath);
			if(!f.exists()){
			    f.getParentFile().mkdirs();
                if (!f.getParentFile().exists()) {
                    throw new IOException("Couldn't create parent directories for full resource path '" + f.getAbsolutePath() + "'");
                }
				if(!f.createNewFile()){
				    throw new IOException("Couldn't create resource file'" + f.getAbsolutePath() + "'");
				}
			}
			if(!f.canWrite()){
				throw new IOException(String.format("Don't have permission to write file '%s' in dir '%s' for root %s. Full path %s",relPath,baseDir.getAbsolutePath(),this, f.getAbsolutePath()));
			}
			if( !f.exists()){
				f.getParentFile().mkdir();
			}
			return new FileOutputStream(f);
		} else {
			throw new IOException(String.format("Couldn't write resource path '%s' for root %s as basedir %s doesn't exist",relPath,this,baseDir));
		}
	}
	
	@Override
	public InputStream getResourceInputStream(String relPath) throws IOException {
		if(isDirectoryAndExists()){
			//TODO:check relPath is in the given directory, no escaping up!
			File f = getByRelPath(relPath);
			if(!f.exists()){
				throw new FileNotFoundException(String.format("Couldn't find file '%s' in dir '%s' for root %s",relPath,baseDir.getAbsolutePath(),this));
			}
			if(!f.canRead()){
				throw new IOException(String.format("Don't have permission to read file '%s' in dir '%s' for root %s",relPath,baseDir.getAbsolutePath(),this));
			}
			return new FileInputStream(f);
		} else {
			throw new IOException(String.format("Couldn't read resource path '%s' for root %s",relPath,this));
		}
	}
	
	@Override
    public String getFullPathInfo(String relPath){
        return getByRelPath(relPath).getAbsolutePath();
    }
	
    @Override
    public RootResource getResource(String relPath) {
        validatePath(relPath);
        return new RootResource(this, relPath);
    }
    
	@Override
    public URL getUrl(String relPath){
        try {
            return getByRelPath(relPath).toURI().toURL();
        } catch (MalformedURLException e) {
            throw new JFindException("couldn't convert relative path '" + relPath + "' to url", e);
        }
    }

    @Override
    public long getLastModified(String relPath) {
        long ts = getByRelPath(relPath).lastModified();
        if (ts <= 0L) {
            ts = Root.TIMESTAMP_NOT_EXIST;
        }
        return ts;
    }
    
    private File getByRelPath(String relpath){
        validatePath(relpath);
		return new File(baseDir.getAbsolutePath(),relpath);
	}
	
	@Override
	public URL toURL(){
	    try {
            return baseDir.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new JFindException("couldn't convert root directory path '" + baseDir + "' to url", e);
        } 
	}

    @Override
    public String getPathName() {
        return PathUtil.toForwardSlashes(baseDir.getAbsolutePath());
    }
	
	public File getPath(){
		return baseDir;
	}
	
	@Override
	public RootType getType(){
		return type;
	}
		
	@Override
	public RootContentType getContentType(){
		return contentType;
	}
	
	private boolean isDirectoryAndExists(){
		return baseDir.exists() && baseDir.isDirectory();
	}

	@Override
	public String toString(){
		return Objects
    		.toStringHelper(this)
    		.add("path", getPathName())
    		.add("type", type)
    		.add("contentType", contentType)
    		.add("isDirectory", baseDir.isDirectory())
    		.add("exists", baseDir.exists())
    		.toString();
	}

	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((baseDir == null) ? 0 : baseDir.hashCode());
	    result = prime * result + ((type == null) ? 0 : type.hashCode());
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
	    DirectoryRoot other = (DirectoryRoot) obj;;
	    if (baseDir == null) {
		    if (other.baseDir != null)
			    return false;
	    } else if (!baseDir.equals(other.baseDir))
		    return false;
	    if (type != other.type)
		    return false;
	    if (contentType != other.contentType)
		    return false;
	    return true;
    }
	
	@Override
	public void accept(RootVisitor visitor) {
		if( visitor.visit(this)){
			visitResources(visitor);
		}
		visitor.endVisit(this);
	}

	private void visitResources(RootVisitor visitor) {
		if(isDirectoryAndExists()){
			visitResources(this,visitor,null,baseDir);
		}
	}

	private static void visitResources(Root root, RootVisitor visitor, String parentPath, File dir) {
		File[] files = dir.listFiles(FILE_FILTER);
		String basePath = parentPath==null?"":(parentPath + "/");
		for (File f : files) {
			String relPath = basePath + f.getName();
			RootResource child = new RootResource(root, relPath);
			visitor.visit(child);
			visitor.endVisit(child);
			if( isCancelled()){
				return;
			}
		}
		File[] childDirs = dir.listFiles(DIR_FILTER);
		for (File childDir : childDirs) {
			visitResources(root, visitor, basePath + childDir.getName(), childDir);
		}
	}
	
	private static boolean isCancelled(){
		return Thread.interrupted();
	}

    private void validatePath(String relPath) {
        if (relPath == null) {
            throw new IllegalArgumentException("Invalid relative path. Expected not null");
        }
        char c;
        for (int i = 0; i < relPath.length(); i++) {
            c = relPath.charAt(i);
            if (c == '|' || c == ';' || (c == '.' && i + 1 < relPath.length() && relPath.charAt(i + 1) == '.')) {
                throw new IllegalArgumentException("Invalid relative path '" + relPath + "'");
            }
        }
    }

    @Override
    public boolean isArchive() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return true;
    }
    
    public static class Builder implements IBuilder<DirectoryRoot> {

        private File baseDir;
        private RootType type;
        private RootContentType contentType;

        @Override
        public DirectoryRoot build() {
            return new DirectoryRoot(baseDir,type,contentType);
        }

        public Builder baseDir(File baseDir) {
            this.baseDir = baseDir;
            return this;
        }

        public Builder type(RootType type) {
            this.type = type;
            return this;
        }

        public Builder contentSrc() {
            contentType(RootContentType.SRC);
            return this;
        }
        
        public Builder contentType(RootContentType contentType) {
            this.contentType = contentType;
            return this;
        }

    }

}
