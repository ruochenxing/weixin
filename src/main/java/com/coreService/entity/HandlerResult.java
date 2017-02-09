package com.coreService.entity;

/**
 * 处理结果
 */
public class HandlerResult{

	private String userId;//用户id
	private String userContent;//用户内容
	private String content;//处理结果
	private String type;//结果类型

	public void setUserId(String userId){
		this.userId=userId;
	}
	public String getUserId(){
		return this.userId;
	}
	public void setUserContent(String userContent){
		this.userContent=userContent;
	}
	public String getUserContent(){
		return this.userContent;
	}
	public void setContent(String content){
		this.content=content;
	}
	public String getContent(){
		return this.content;
	}
	public void setType(String type){
		this.type=type;
	}
	public String getType(){
		return this.type;
	}
}