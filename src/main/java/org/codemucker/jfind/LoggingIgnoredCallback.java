package org.codemucker.jfind;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.log4j.Logger;
import org.codemucker.jfind.JFind.IgnoredCallback;


public class LoggingIgnoredCallback implements IgnoredCallback {
	private final Logger logger;
	
	public LoggingIgnoredCallback(){
		this(Logger.getLogger(LoggingErrorCallback.class));
	}
	
	public LoggingIgnoredCallback(Logger logger){
		this.logger = checkNotNull(logger, "expect logger");
	}
	
	@Override
    public void onArchiveIgnored(RootResource archiveFile) {
		logger.info("ignoring archive:" + archiveFile);
    }

	@Override
    public void onResourceIgnored(RootResource resource) {
		logger.info("ignoring resource:" + resource);
	}

	@Override
    public void onClassNameIgnored(RootResource resource,String className) {
		logger.info("ignoring class named:" + className);
	}

	@Override
    public void onClassIgnored(RootResource resource,Class<?> ignoredClass) {
		logger.info("ignoring class:" + ignoredClass.getName());
		
	}

	@Override
    public void onRootIgnored(Root root) {
		logger.info("ignoring class path:" + root);
	}

	@Override
    public void onIgnored(Object obj) {
    }
}