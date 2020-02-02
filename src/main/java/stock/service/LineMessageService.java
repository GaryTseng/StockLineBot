package stock.service;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import stock.vo.stock.Stock;

public class LineMessageService {

	private String accessToken = "testAccessToken";

	private StockService stockService = new StockService();

	private static HashMap<String, String> _stockMap;
	private static Date _stockUpDate;

	public HashMap<String, String> getStockMap() {
		Date today = new Date();
		if (_stockMap == null || _stockUpDate == null || _stockUpDate.before(today)) {
			_stockMap = stockService.GetAllStockList();
			_stockUpDate = new Date(today.getTime() + (1000 * 60 * 60 * 24));
		}

		return _stockMap;
	}

	public void SendResponseMessages(String replyToken, String message) {
		try {
			message = message.trim();

			int length = message.length();
			if (length <= 2 || !message.startsWith("#")) {
				return;
			}

			String allTextString = message.substring(1, length);
			Map<String, String> allStockMap = getStockMap();
			allTextString = allTextString.trim();

			String stockID = "";
			if (allStockMap.containsKey(allTextString)) {
				stockID = allTextString;
			} else if (allStockMap.containsValue(allTextString)) {
				for (String o : allStockMap.keySet()) {
					if (allStockMap.get(o).toLowerCase().equals(allTextString.toLowerCase())) {
						stockID = o;
						break;
					}
				}
			}
			if (stockID == "") {
				return;
			}

			String altText = "StockBot傳送一則訊息";
			Stock st = stockService.GetStockInfo(stockID);
			String StockMsg;
			if (st != null) {

				StringBuffer sb = new StringBuffer();
				String blueColor = "#0000CC";
				String greenColor = "#008844";
				String redColor = "#ff0000";
				String greyColor = "#888888";
				String priceColor = greyColor;

				if (st.Change.startsWith("▽")) {
					priceColor = greenColor;
				} else if (st.Change.startsWith("▲") || st.Change.startsWith("△")) {
					priceColor = redColor;
				}
				altText = st.Name + " " + st.Date.replace("　", "") + " " + st.Time + " 成交價：" + st.DealPrice;
				sb.append("{   \"type\": \"text\",   \"text\": \"" + st.Date.replace("　", "") + " " + st.Time + "\"  }")
						.append(",");
				sb.append("{   \"type\": \"text\",  \"color\": \"" + blueColor + "\",  \"text\": \"股票：" + st.Name
						+ "\"  }").append(",");
				sb.append("{   \"type\": \"text\",   \"color\": \"" + priceColor + "\",  \"text\": \"漲跌：" + st.Change
						+ "\"  }").append(",");
				sb.append("{   \"type\": \"text\",   \"text\": \"成交：" + st.DealPrice + " ; 張數：" + st.Quantity + "\"  }")
						.append(",");
				sb.append("{   \"type\": \"text\",   \"text\": \"買進：" + st.BuyPrice + " ; 賣出：" + st.SellPrice + "\"  }")
						.append(",");
				sb.append("{   \"type\": \"text\",   \"text\": \"最高：" + st.Top + " ; 最低：" + st.Low + "\"  }")
						.append(",");
				sb.append("{   \"type\": \"text\",   \"text\": \"開盤：" + st.First + "\"  }");

				StockMsg = sb.toString();
			} else {
				StockMsg = "{   \"type\": \"text\",   \"text\": \"查無資料\"  }";
			}

			message = "[    {      \"type\": \"flex\",      \"altText\": \"" + altText
					+ "\",      \"contents\": {        \"type\": \"bubble\",        \"body\": {          \"type\": \"box\",          \"layout\": \"vertical\",          \"contents\": [     "
					+ StockMsg + "     ]        }      }    }  ]}";

			message = "{\"replyToken\":\"" + replyToken + "\",\"messages\":" + message + "}"; // 回傳的json格式訊息
			URL myurl = new URL("https://api.line.me/v2/bot/message/reply"); // 回傳的網址
			HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection(); // 使用HttpsURLConnection建立https連線
			con.setRequestMethod("POST");// 設定post方法
			con.setRequestProperty("Content-Type", "application/json; charset=utf-8"); // 設定Content-Type為json
			con.setRequestProperty("Authorization", "Bearer " + this.accessToken); // 設定Authorization
			con.setDoOutput(true);
			con.setDoInput(true);
			DataOutputStream output = new DataOutputStream(con.getOutputStream()); // 開啟HttpsURLConnection的連線
			output.write(message.getBytes(Charset.forName("utf-8"))); // 回傳訊息編碼為utf-8
			output.close();
			System.out.println("Resp Code:" + con.getResponseCode() + "; Resp Message:" + con.getResponseMessage()); // 顯示回傳的結果，若code為200代表回傳成功
		} catch (MalformedURLException e) {
			System.out.println("Message: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Message: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
