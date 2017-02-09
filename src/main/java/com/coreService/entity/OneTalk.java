package com.coreService.entity;

import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 一次对话
 */
public class OneTalk {
	private final User user = new User();
	private final CopyOnWriteArrayList<Statement> statementsByUser = new CopyOnWriteArrayList<Statement>();
	private final CopyOnWriteArrayList<Statement> statementsByMe = new CopyOnWriteArrayList<Statement>();
	private final AtomicInteger userStatementCount = new AtomicInteger(0);
	private final AtomicInteger meStatementCount = new AtomicInteger(0);
	private final Date createDate = new Date();
	private volatile int check = 0;

	public OneTalk() {
	}

	public OneTalk(String userId) {
		user.setUserId(userId);
	}

	public void setUser(User user) {
		this.user.setUserId(user.getUserId());
		this.user.setName(user.getName());
		this.user.setAge(user.getAge());
		this.user.setSex(user.getSex());
		this.user.setAddress(user.getAddress());
		this.user.setLike(user.getLike());
	}

	public synchronized void addUserStatement(String content, long relation_id, String replyByWho) {
		int id = userStatementCount.incrementAndGet();
		statementsByUser.add(new Statement(id, content, relation_id, replyByWho));
	}

	public synchronized Statement getLastUserStatement() {
		return statementsByUser.get(userStatementCount.get() - 1);
	}

	public void addMeStatement(String content, long relation_id, String replyByWho) {
		int id = meStatementCount.incrementAndGet();
		statementsByMe.add(new Statement(id, content, relation_id, replyByWho));
	}

	public User getUser() {
		return user;
	}

	public int getCheck() {
		return check;
	}

	public void setCheck(int check) {
		this.check = check;
	}

	public Date getCreateDate() {
		return createDate;
	}
}