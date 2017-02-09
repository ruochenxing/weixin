package com.coreService.listener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.coreService.util.Constants;
import com.coreService.util.FilterManager;
import com.coreService.util.TalkManager;
import com.coreService.util.TimerManager;
public class WeixinServletContextListener implements ServletContextListener {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(WeixinServletContextListener.class);
	public void contextInitialized(ServletContextEvent event) {
		logger.info("init");
		TalkManager.init();
		FilterManager.init();
		if(Constants.AUTORECYCLE){
			new TimerManager();
		}
		else{
			logger.info("auto recycle task closed");
		}
	}
	public void contextDestroyed(ServletContextEvent event) {
	}
}