package stock.service;

import java.io.IOException;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import stock.util.HtmlParser;
import stock.vo.stock.Stock;

public class StockService {

	public Stock GetStockInfo(String stockID) {

		String url = "https://tw.stock.yahoo.com/q/q?s=" + stockID;

		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Stock stockObj = new Stock();
		stockObj.Date = doc.select("table[border=0]").select("td[width=160]").select("font").first().text().trim();
		Elements tds = doc.select("table[border=2]").select("td[width=105]").parents().select("td");
		int index = 0;
		for (Element td : tds) {

			try {
				String tdText = td.text();
				switch (index) {
				case 0:
					int emptyIndex = tdText.indexOf(" ");
					stockObj.Name = tdText.substring(0, emptyIndex);
					break;
				case 1:
					stockObj.Time = tdText;
					break;
				case 2:
					stockObj.DealPrice = tdText;
					break;
				case 3:
					stockObj.BuyPrice = tdText;
					break;
				case 4:
					stockObj.SellPrice = tdText;
					break;
				case 5:
					stockObj.Change = tdText;
					break;
				case 6:
					stockObj.Quantity = tdText;
					break;
				case 7:
					stockObj.PriceYestoday = tdText;
					break;
				case 8:
					stockObj.First = tdText;
					break;
				case 9:
					stockObj.Top = tdText;
					break;
				case 10:
					stockObj.Low = tdText;
					break;

				}
			} finally {
			}
			index++;
		}
		return stockObj;
	}

	public HashMap<String, String> GetAllStockList() {
		HashMap<String, String> stockObj = GetStockExchangeStockList();
		stockObj.putAll(GetOverTheCounterStockList());
		return stockObj;
	}

	// 上市
	public HashMap<String, String> GetStockExchangeStockList() {
		String url = "http://isin.twse.com.tw/isin/C_public.jsp?strMode=2";

		Document doc = HtmlParser.GetDocument(url);

		HashMap<String, String> stockObj = new HashMap<String, String>();

		Elements trs = doc.select("table[class='h4']").select("tr");
		boolean skipFirstIndex = true;
		for (Element tr : trs) {
			if (skipFirstIndex) {
				skipFirstIndex = !skipFirstIndex;
				continue;
			}
			try {
				if (tr.select("td") == null || tr.select("td").first() == null) {
					continue;
				}
				String tdText = tr.select("td").first().text();

				String[] tt = tdText.split("　");
				if (tt.length >= 2) {

					if (!stockObj.containsKey(tt[0])) {
						stockObj.put(tt[0].trim(), tt[1].trim());
					}
				}
			} finally {
			}
		}
		return stockObj;
	}

	// 上櫃
	public HashMap<String, String> GetOverTheCounterStockList() {

		String url = "https://isin.twse.com.tw/isin/C_public.jsp?strMode=4";
		Document doc = HtmlParser.GetDocument(url);

		HashMap<String, String> stockObj = new HashMap<String, String>();

		Elements trs = doc.select("table[class='h4']").select("tr");
		boolean skipFirstIndex = true;
		for (Element tr : trs) {
			if (skipFirstIndex) {
				skipFirstIndex = !skipFirstIndex;
				continue;
			}
			try {
				if (tr.select("td") == null || tr.select("td").first() == null) {
					continue;
				}
				String tdText = tr.select("td").first().text();

				String[] tt = tdText.split("　");
				if (tt.length >= 2) {

					if (!stockObj.containsKey(tt[0])) {
						stockObj.put(tt[0].trim(), tt[1].trim());
					}
				}
			} finally {
			}
		}
		return stockObj;
	}

}
