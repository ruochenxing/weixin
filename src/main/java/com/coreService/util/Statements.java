package com.coreService.util;
import java.util.*;
public final class Statements{
	public static final String REPLY_SAD[]={"/::~","/:8*肿么了？","/::(","/::'(","/::<","/::(","/::'"};
	public static final String REPLY_COOL[]={"要不要这么高冷","/::)","/:8-)","/::+","/:dig","/:strong"};
	public static final String REPLY_HAPPY[]={"这么开心，捡钱了么？","/:circle什么事这么开心？","/:,@P","/::P","/::D","/:,@-D","/::>","/:<O>","/:circle","/:jump"};
	public static final Random random=new Random(47);
	public static String getReplySad(){
		return REPLY_SAD[random.nextInt(REPLY_SAD.length)];
	}
	public static String getReplyCool(){
		return REPLY_COOL[random.nextInt(REPLY_COOL.length)];
	}
	public static String getReplyHappy(){
		return REPLY_HAPPY[random.nextInt(REPLY_HAPPY.length)];
	}
}