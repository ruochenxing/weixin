package com.weixin.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class MyClient {
	private HttpClient client = wrapClient(HttpClients.createDefault());
	private CookieStore cookieStore = new BasicCookieStore();
	private HttpClientContext context = HttpClientContext.create();
	private HttpPost httpPost = null;
	private HttpResponse response = null;
	private HttpGet httpGet;
	private String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36";
	private String iosUserAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A300 Safari/602.1";

	public MyClient() {
		context.setCookieStore(cookieStore);
	}

	public static void main(String[] args) {
	}

	public static String parseUk(String url) {
		MyClient client = new MyClient();
		Map<String, String> header = new HashMap<String, String>();
		header.put("User-Agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36");
		header.put("Host", "pan.baidu.com");
		header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		header.put("Accept-Encoding", "gzip, deflate, sdch, br");
		header.put("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4,ja;q=0.2");
		header.put("Cache-Control", "max-age=0");
		header.put("Connection", "keep-alive");
		header.put("Upgrade-Insecure-Requests", "1");
		String content = client.getHtml(url, false, header);
		if (content.contains("页面不存在")) {
			content = client.getHtml(url, false, header);
		}
		Pattern pattern = Pattern.compile("\"uk\":(\\d{10}),");
		Matcher matcher = pattern.matcher(content);
		String uk = null;
		while (matcher.find()) {
			uk = matcher.group(1);
		}
		if (uk == null) {
			uk = "";
		}
		return uk;
	}

	/**
	 * 避免HttpClient的”SSLPeerUnverifiedException: peer not authenticated”异常
	 * 不用导入SSL证书
	 * 
	 * @param base
	 * @return
	 */
	public static HttpClient wrapClient(HttpClient base) {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLConnectionSocketFactory ssf = new SSLConnectionSocketFactory(ctx, NoopHostnameVerifier.INSTANCE);
			CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(ssf).build();
			return httpclient;
		} catch (Exception ex) {
			ex.printStackTrace();
			return HttpClients.createDefault();
		}
	}

	public List<Cookie> getCookies() {
		List<Cookie> cookies = context.getCookieStore().getCookies();
		if (cookies.isEmpty()) {
			return null;
		} else {
			return cookies;
		}
	}

	public String doGetSaveByImage(String path, String url) throws ClientProtocolException, IOException {
		httpGet = new HttpGet(url);
		response = client.execute(httpGet, context);
		HttpEntity entity = response.getEntity();
		byte[] allbuf = EntityUtils.toByteArray(entity);
		if (allbuf.length == 0) {
			return "error";
		} else {
			InputStream sbs = new ByteArrayInputStream(allbuf);
			BufferedInputStream in1 = new BufferedInputStream(sbs);
			String s1 = path + "/" + UUID.randomUUID().toString() + ".jpg";
			File img = new File(s1);
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(img));
			byte[] buf = new byte[1024];
			int length = in1.read(buf);
			while (length != -1) {
				out.write(buf, 0, length);
				length = in1.read(buf);
			}
			in1.close();
			out.close();
			return s1;
		}
	}

	private List<NameValuePair> mapToList(Map<String, String> map) {
		List<NameValuePair> nvps = null;
		if (map != null && map.size() != 0) {
			nvps = new ArrayList<NameValuePair>();
			Set<String> key = map.keySet();
			String s = null;
			for (Iterator<String> it = key.iterator(); it.hasNext();) {
				s = it.next();
				nvps.add(new BasicNameValuePair(s, map.get(s)));
			}
		}
		return nvps;
	}

	public String doPostNeedLocation(String url, Map<String, String> map, String prefixUrl, boolean useiOS,
			Map<String, String> headers) throws ParseException, IOException {
		List<NameValuePair> nvps = mapToList(map);
		return doPostNeedLocation(url, nvps, prefixUrl, useiOS, headers);
	}

	public String doPostNeedLocation(String url, List<NameValuePair> nvps, String prefixUrl, boolean useiOS,
			Map<String, String> headers) throws ParseException, IOException {
		httpPost = new HttpPost(url);
		httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
		response = client.execute(httpPost, context);
		Header locationHeader = response.getFirstHeader("Location");
		return getHtml(prefixUrl + locationHeader.getValue(), useiOS, headers);
	}

	public String doPost(String url, Map<String, String> map, boolean useiOS) throws ParseException, IOException {
		List<NameValuePair> nvps = mapToList(map);
		return doPost(url, nvps, useiOS);
	}

	public String doPost(String url, List<NameValuePair> nvps, boolean useiOS) throws ParseException, IOException {
		httpPost = new HttpPost(url);
		httpPost.setHeader("User-Agent", useiOS ? iosUserAgent : userAgent);
		httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
		response = client.execute(httpPost, context);
		HttpEntity entity = response.getEntity();
		String html = EntityUtils.toString(entity);
		return html;
	}

	public String getHtml(String URL, boolean useiOS, Map<String, String> headers) {
		httpGet = new HttpGet(URL);
		httpGet.setHeader("User-Agent", useiOS ? iosUserAgent : userAgent);
		if (headers != null && !headers.isEmpty()) {
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				httpGet.setHeader(entry.getKey(), entry.getValue());
			}
		}
		String res = "";
		try {
			response = client.execute(httpGet, context);
			if (response.getEntity() != null)
				res = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
			else
				res = "";
			return res;
		} catch (IOException e) {
			return (e.toString());
		} catch (ParseException e) {
			return (e.toString());
		}
	}

	public HttpResponse getResponse() {
		return response;
	}

	public void setResponse(HttpResponse response) {
		this.response = response;
	}

	public HttpClient getClient() {
		return client;
	}

	public void setClient(HttpClient client) {
		this.client = client;
	}

}