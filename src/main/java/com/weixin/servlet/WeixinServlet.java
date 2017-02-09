package com.weixin.servlet;

import com.github.sd4324530.fastweixin.servlet.*;

@SuppressWarnings("serial")
public class WeixinServlet extends WeixinServletSupport {
	@Override
	protected WeixinSupport getWeixinSupport() {
		return new MyServletWeixinSupport();
	}
}