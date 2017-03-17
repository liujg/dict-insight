/**
 * @(#)HttpUtils.java, 2014年8月1日. Copyright 2014 Yodao, Inc. All rights
 *                     reserved. YODAO PROPRIETARY/CONFIDENTIAL. Use is subject
 *                     to license terms.
 */
package dictinsight.utils.io;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author liujg
 */
public class HttpUtils {
	private static String SEND_MESSAGE_URL=null;

	private static final String SUCCESS = "issucc";

    public static List<String> getSvnConfServer(String url) {
        BufferedReader reader = null;
        HttpURLConnection connection = null;
        List<String> seedList = new ArrayList<String>();
        try {
            URL srcUrl = new URL(url);
            connection = (HttpURLConnection) srcUrl.openConnection();
            connection.setConnectTimeout(1000 * 10);
            connection.connect();
            reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String rec;
            while ((rec = reader.readLine()) != null) {
                seedList.add(rec);
            }
        } catch (Exception e) {
            System.out.println("get date from " + url + " error!");
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (connection != null)
                connection.disconnect();
        }
        return seedList;
    }
	   
	public static String getDataFromOtherServer(String url) {
		String rec = null;
		BufferedReader reader = null;
		HttpURLConnection connection = null;
		try {
			URL srcUrl = new URL(url);
			connection = (HttpURLConnection) srcUrl.openConnection();
			connection.setConnectTimeout(1000 * 10);
			connection.connect();
			reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			rec = reader.readLine();
		} catch (Exception e) {
			System.out.println("get date from " + url + " error!");
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (connection != null)
				connection.disconnect();
		}
		return rec;
	}

	public static String getDataFromOtherServer(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			URLConnection conn = realUrl.openConnection();
			conn.setConnectTimeout(1000 * 10);
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			out = new PrintWriter(conn.getOutputStream());
			out.print(param);
			out.flush();
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("get date from " + url + param + " error!");
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static boolean postData(String[] keys, String[] values) {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpParams params = client.getParams();
		HttpConnectionParams.setSoTimeout(params, 1000 * 60);
		HttpConnectionParams.setConnectionTimeout(params, 1000 * 5);
		if(null==SEND_MESSAGE_URL)
		    SEND_MESSAGE_URL = FileUtils.getStringConfig(
	            "course-server.conf", "pushHost",
	            "http://livetest.youdao.com/pushMsgSingle");
		HttpPost post = new HttpPost(SEND_MESSAGE_URL);
		try {
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			if (keys != null && values != null) {
				if (keys.length != values.length) {
					return false;
				} else {
					for (int i = 0; i < keys.length; i++) {
						nvps.add(new BasicNameValuePair(keys[i], values[i]));
					}
				}
			}
			post.setEntity(new UrlEncodedFormEntity(nvps));

			HttpResponse response = client.execute(post);
			int code = response.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				String strResult = EntityUtils.toString(response.getEntity());
				JSONObject result = JSONObject.parseObject(strResult);
				if (result == null) {
					return false;
				}
				int suc = result.getIntValue(SUCCESS);
				return suc == 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}
		return false;
	}

	/**
	 * 使用https发送post请求
	 * @param url
	 * @param param
	 * @return post请求的响应
	 */
	public static String httpsPostData(String url, String param) {
		  class DefaultTrustManager implements X509TrustManager {
			  @Override
			  public void checkClientTrusted(X509Certificate[] arg0, String arg1)
					  throws CertificateException{}

			  @Override
			  public void checkServerTrusted(X509Certificate[] arg0, String arg1)
					  throws CertificateException {}

			  @Override
			  public X509Certificate[] getAcceptedIssuers() { return null; }
		}

		BufferedOutputStream brOutStream = null;
		BufferedReader reader = null;

		try {
			SSLContext context = SSLContext.getInstance("SSL");
			context.init(null, new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());
			HttpsURLConnection connection = (HttpsURLConnection) (new URL(url)).openConnection();
			connection.setSSLSocketFactory(context.getSocketFactory());
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Proxy-Connection", "Keep-Alive");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setConnectTimeout(1000 * 15);

			brOutStream = new BufferedOutputStream(connection.getOutputStream());
			brOutStream.write(param.getBytes());
			brOutStream.flush();

			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String responseContent = "";
			String line = reader.readLine();
			while(line != null) {
				responseContent += line;
				line = reader.readLine();
			}

			return responseContent;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(brOutStream != null)
					brOutStream.close();
				if(reader != null)
					reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
