package com.coreService.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class EmojiUtil {
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(EmojiUtil.class);
	private static int counter = 0;
	public static final Random random = new Random(47);
	public static final String EMOJI[] = { "/::~", "/:,@P", "/::P", "/::D", "/:,@-D", "/::Q", "/::@", "/::>", "/:<O>",
			"/::B", "/:circle", "/:jump", "/::'(", "/::<", "/::(", "/::'", "/::)", "/:8-)", "/::+", "/:dig", "/:strong",
			"/::$" };
	// 开心的emoji
	public static final String EMOJI_HAPPY[] = { "/:,@P", "/::P", "/::D", "/:,@-D", "/::>", "/:<O>", "/:circle",
			"/:jump", "/::$", "/::B" };
	// 不开心的emoji
	public static final String EMOJI_SAD[] = { "/::'(", "/::<", "/::(", "/::~", "/::Q", "/::@" };
	// 高冷
	public static final String EMOJI_COOL[] = { "/::)", "/:8-)", "/::+", "/:dig", "/:strong" };

	public static String reply(String str) {
		logger.debug("reply emoji :" + str);
		int result[] = containsEmoji(str);
		if (result[0] == -1) {
			return str;
		}
		int index = result[0];
		for (int i = 0; i < EMOJI_HAPPY.length; i++) {
			if (EMOJI[index].equals(EMOJI_HAPPY[i])) {
				return Statements.getReplyHappy();
			}
		}
		for (int i = 0; i < EMOJI_SAD.length; i++) {
			if (EMOJI[index].equals(EMOJI_SAD[i])) {
				return Statements.getReplySad();
			}
		}
		for (int i = 0; i < EMOJI_COOL.length; i++) {
			if (EMOJI[index].equals(EMOJI_COOL[i])) {
				return Statements.getReplyCool();
			}
		}
		return str;
	}

	// 是否包含emoji表情，并返回出现频数最多的一个下标
	private static int[] containsEmoji(String str) {
		Map<Integer, Integer> numMap = new LinkedHashMap<Integer, Integer>();
		if (str != null && str.trim().length() > 0) {
			for (int i = 0; i < EMOJI.length; i++) {
				if (str.contains(EMOJI[i])) {
					numMap.put(i, stringNumbers(str, EMOJI[i]));
					counter = 0;
				}
			}
		}
		Map<Integer, Integer> resultMap = sortMapByValue(numMap);
		if (resultMap == null) {
			return new int[] { -1, -1 };
		}
		Set<Map.Entry<Integer, Integer>> keys = resultMap.entrySet();
		Iterator<Map.Entry<Integer, Integer>> it = keys.iterator();
		if (it.hasNext()) {
			Map.Entry<Integer, Integer> entry = it.next();
			return new int[] { entry.getKey(), entry.getValue() };
		}
		return new int[] { -1, -1 };
	}

	// 替换掉所有的emoji表情
	public static String removeEmoji(String str) {
		if (str != null && str.trim().length() > 0) {
			for (int i = 0; i < EMOJI.length; i++) {
				if (str.contains(EMOJI[i])) {
					if (EMOJI[i].equals("/::)")) {
						str = str.replaceAll("/::\\)", "");
					} else if (EMOJI[i].equals("/:8-)")) {
						str = str.replaceAll("/:8-\\)", "");
					} else if (EMOJI[i].equals("/::(")) {
						str = str.replaceAll("/::\\(", "");
					} else if (EMOJI[i].equals("/::'(")) {
						str = str.replaceAll("/::'\\(", "");
					} else {
						str = str.replaceAll(EMOJI[i], "");
					}
					if (str.trim().length() <= 0) {
						break;
					}
				}
			}
		}
		return str;
	}

	/**
	 * 使用 Map按value进行排序
	 * 
	 * @param map
	 * @return
	 */
	public static Map<Integer, Integer> sortMapByValue(Map<Integer, Integer> map) {
		if (map == null || map.isEmpty()) {
			return null;
		}
		Map<Integer, Integer> sortedMap = new LinkedHashMap<Integer, Integer>();
		List<Map.Entry<Integer, Integer>> entryList = new ArrayList<Map.Entry<Integer, Integer>>(map.entrySet());
		Collections.sort(entryList, new MapValueComparator());
		Iterator<Map.Entry<Integer, Integer>> iter = entryList.iterator();
		Map.Entry<Integer, Integer> tmpEntry = null;
		while (iter.hasNext()) {
			tmpEntry = iter.next();
			sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
		}
		return sortedMap;
	}

	// 比较器类
	public static class MapValueComparator implements Comparator<Map.Entry<Integer, Integer>> {
		public int compare(Map.Entry<Integer, Integer> me1, Map.Entry<Integer, Integer> me2) {
			return me2.getValue().compareTo(me1.getValue());
		}
	}

	public static int stringNumbers(String content, String str) {
		if (content.indexOf(str) == -1) {
			return 0;
		} else {
			counter++;
			stringNumbers(content.substring(content.indexOf(str) + str.length()), str);
			return counter;
		}
	}
}