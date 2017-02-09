package com.weixin.util;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;

/**
 * 邮件发送工具类
 */
@SuppressWarnings("deprecation")
public class SmtpHelper {
  private static Executor mailSendeExecutor = Executors.newCachedThreadPool();
    private final static Properties smtp_props = new Properties();

    public static void main(String[] args) {
        send(GlobalConstants.HOST_EMAIL, "测试邮件发送1", "hao");
    }

    static {
        Properties prop = new Properties();
        try {
            prop.load(SmtpHelper.class.getResourceAsStream("smtp.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Unabled to load smtp.properties", e);
        }
        for (Object key : prop.keySet()) {
            String skey = (String) key;
            if (skey.startsWith("smtp.")) {
                String name = skey.substring(5);
                smtp_props.put(name, prop.getProperty(skey));
            }
        }
        System.getProperties().put("mail.smtp.quitwait", false);
    }

    /**
     * 多线程发送邮件
     * @param email
     */
    public static void sendInExecutor(final String email, final String title, final String content) {
        mailSendeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    send(email, title, content);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 发送邮件
     * @param email
     * @param title
     * @param content
     */
    public static void send(String email, String title, String content) {
        HtmlEmail body = null;
        try {
            body = (HtmlEmail) _NewMailInstance(Arrays.asList(email), true);
            body.setSubject(title);
            body.setHtmlMsg(content);
            body.send();
        } catch (EmailException e) {
            throw new RuntimeException("Unabled to send mail", e);
        }
    }

    /**
     * 发送个性化邮件
     * @param email
     * @param title
     * @param content
     * @param from
     */
    public static void send(String email, String title, String content, String from) {
        try {
            HtmlEmail body = (HtmlEmail) _NewMailInstance(Arrays.asList(email), true);
            body.setSubject(title);
            body.setHtmlMsg(content);
            if (from == null || from.isEmpty()) {
                from = "ruochenxing";
            }
            body.setFrom("fuck@163.com", from);
            body.send();
        } catch (EmailException e) {
            throw new RuntimeException("Unabled to send mail", e);
        }
    }

    public static void sendText(String email, String title, String content) {
        try {
            SimpleEmail body = (SimpleEmail) _NewMailInstance(Arrays.asList(email), false);
            body.setSubject(title);
            body.setMsg(content);
            body.send();
        } catch (EmailException e) {
            throw new RuntimeException("Unabled to send mail", e);
        }
    }

    public static void SendHtmlMail(List<String> emails, String title, String html) throws EmailException {
        HtmlEmail mail = (HtmlEmail) _NewMailInstance(emails, true);
        try {
            mail.setSubject(title);
            mail.setHtmlMsg(html);
            mail.send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * 初始化邮件
     * @param emails
     *            所有接收者
     * @param html
     * @return
     * @throws EmailException
     */
    private final static Email _NewMailInstance(List<String> emails, boolean html) throws EmailException {
        Email body = html ? new HtmlEmail() : new SimpleEmail();
        body.setCharset("UTF-8");
        body.setHostName(smtp_props.getProperty("hostname"));
        body.setSmtpPort(Integer.parseInt(smtp_props.getProperty("port")));
        body.setSSL("true".equalsIgnoreCase(smtp_props.getProperty("ssl")));
        body.setAuthentication(smtp_props.getProperty("username"), smtp_props.getProperty("password"));
        String[] senders = (smtp_props.getProperty("sender").split(":"));
        body.setFrom(senders[1], senders[0]);
        for (String m : emails) {
            body.addTo(m);
        }
        return body;
    }
}

