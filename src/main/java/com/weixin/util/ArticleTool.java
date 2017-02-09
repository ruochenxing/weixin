package com.weixin.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.sd4324530.fastweixin.message.Article;

public class ArticleTool {

	public static final MyClient CLIENT = new MyClient();
	public static long refreshTime = 0l;
	public static final long _1d = 1000 * 24 * 60 * 60L;
	public static List<Article> DATAS = new ArrayList<>();
	public static Map<String, Integer> COUNTER = new HashMap<>();
	public static final int DEFAULT_SIZE = 5;

	public static void main(String[] args) {
		System.out.println(replySearch("驴得水"));
	}

	public static Article getOne(String userName) {
		if (!isToday(refreshTime) || DATAS.isEmpty()) {
			try {
				refreshData(5);
				COUNTER = new HashMap<String, Integer>();
			} catch (Exception e) {
				return null;
			}
		}
		int count = 0;
		if (COUNTER.containsKey(userName)) {
			count = COUNTER.get(userName) + 1;
		}
		COUNTER.put(userName, count);
		if (DATAS.isEmpty() || count > DATAS.size() - 1 || count < 0) {
			return null;
		}
		return DATAS.get(count);
	}

	public static List<Article> refreshData(int size) {
		if (size <= 0)
			size = DEFAULT_SIZE;
		String result = reqData(size);
		if (result != null && result.length() > 0) {
			JSONObject jsonObject = JSONObject.parseObject(result);
			if (jsonObject.getBooleanValue("result")) {
				DATAS = JSON.parseArray(jsonObject.getString("data"), Article.class);
			}
		}
		return DATAS;
	}

	public static String reqData(int size) {
		String result = CLIENT.getHtml("http://www.daiguangwang.top/api/l/" + size, false, null);
		refreshTime = new Date().getTime();
		return result;
	}

	/**
	 * 搜索电影
	 */
	public static String searchMovie(String key) {
		String result = CLIENT.getHtml("http://www.zuilihai.top/action/api/s?key=" + key, false, null);
		return result;
	}

	public static List<Article> parseMovie(String key) {
		List<Article> articles = new ArrayList<Article>();
		String result = searchMovie(key);
		JSONObject jsonObject = JSONObject.parseObject(result);
		if (jsonObject.getBooleanValue("result") && jsonObject.getInteger("totalCount") > 0) {
			JSONArray array = JSONArray.parseArray(jsonObject.getString("data"));
			for (int i = 0; i < array.size(); i++) {
				JSONObject o = (JSONObject) array.get(i);
				Article a = new Article();
				a.setTitle(o.getString("title"));
				String uk = o.getString("uk");
				String shareid = o.getString("shareid");
				String url = String.format("pan.baidu.com/share/link?uk=%s&third=0&shareid=%s", uk, shareid);
				a.setUrl(url);
				articles.add(a);
			}
			return articles;
		} else {
			return articles;
		}
	}

	public static String replySearch(String key) {
		List<Article> articles = parseMovie(key);
		if (articles == null || articles.isEmpty()) {
			return "抱歉，未找到数据，找到后将立刻通知你";
		} else if (articles.size() > 10) {
			articles = articles.subList(0, 10);
		}
		StringBuffer sb = new StringBuffer();
		for (Article a : articles) {
			sb.append(a.getTitle() + ":" + a.getUrl() + "\n");
		}
		return sb.toString();
	}

	/**
	 * 是否是今天
	 * 
	 * @param date
	 * @return
	 */
	public static boolean isToday(long date) {
		Date day = new Date();
		return date >= dayBegin(day).getTime() && date <= dayEnd(day).getTime();
	}

	/**
	 * 获取指定时间的那天 00:00:00.000 的时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date dayBegin(final Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	/**
	 * 获取指定时间的那天 23:59:59.999 的时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date dayEnd(final Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		return c.getTime();
	}
}
