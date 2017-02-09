package com.coreService.entity;
/**
 * 句子
 * 不可变对象
 */
public class Statement{

	private final long id;
	private final String content;
	private final long relation_id;//准备弃用
	private final String replyByWho;
	public Statement(long id,String content,long relation_id,String replyByWho){
		this.id=id;
		this.content=content;
		this.relation_id=relation_id;
		this.replyByWho=replyByWho;
	}
	public String getContent(){
		return content;
	}
	public long getId() {
		return id;
	}
	public long getRelation_id() {
		return relation_id;
	}
	public String getReplyByWho() {
		return replyByWho;
	}
}