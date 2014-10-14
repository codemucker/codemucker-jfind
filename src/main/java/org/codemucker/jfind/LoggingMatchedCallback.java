package org.codemucker.jfind;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.log4j.Logger;
import org.codemucker.jfind.JFind.MatchedCallback;


public class LoggingMatchedCallback implements MatchedCallback {
	private final Logger logger;
	
	public LoggingMatchedCallback(){
		this(Logger.getLogger(LoggingMatchedCallback.class));
	}
	
	public LoggingMatchedCallback(Logger logger){
		this.logger = checkNotNull(logger, "expect logger");
	}
	
	@Override
	public void onArchiveMatched(RootResource archiveFile) {
		logger.info("matched archive:" + archiveFile);
	}

	@Override
	public void onResourceMatched(RootResource resource) {
		logger.info("matched resource:" + resource);
	}

	@Override
	public void onClassNameMatched(RootResource resource,String className) {
		logger.info("matched className:" + className);
	}

	@Override
	public void onRootMatched(Root root) {
		logger.info("matched class path root:" + root);
	}

	@Override
	public void onClassMatched(RootResource resource,Class<?> matchedClass) {
		logger.info("matched class:" + matchedClass.getName());
	}

	@Override
    public void onMatched(Object obj) {
    }
}