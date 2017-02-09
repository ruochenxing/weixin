package com.coreService.util;
import java.util.LinkedList;
import java.util.List;

import com.coreService.filter.BaseFilter;
import com.coreService.filter.impl.EmojiFilter;
import com.coreService.filter.impl.WeatherFilter;
public class FilterManager{
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(FilterManager.class);
	private static final List<BaseFilter> filters=new LinkedList<BaseFilter>();
	public static void init(){
		logger.info("init");
		filters.add(EmojiFilter.ME);
		filters.add(WeatherFilter.ME);
	}
	public static List<BaseFilter> getFilters(){
		return filters;
	}
}