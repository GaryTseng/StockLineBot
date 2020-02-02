package stock.util;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HtmlParser {

	public static Document GetDocument(String url) {

		Document doc = null;
		try {
			// 直接通過主機認證
			HostnameVerifier hv = new HostnameVerifier() {
				public boolean verify(String urlHostName, SSLSession session) {
					return true;
				}
			};
			// 配置認證管理器
			javax.net.ssl.TrustManager[] trustAllCerts = { new TrustAllTrustManager() };
			SSLContext sc = null;
			try {
				sc = SSLContext.getInstance("SSL");
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SSLSessionContext sslsc = sc.getServerSessionContext();
			sslsc.setSessionTimeout(0);
			try {
				sc.init(null, trustAllCerts, null);
			} catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			// 設定主機認證
			HttpsURLConnection.setDefaultHostnameVerifier(hv);
			Connection con = Jsoup.connect(url).timeout(60000).maxBodySize(0);

			doc = con.get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return doc;
	}
}
