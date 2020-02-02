package stock.controller;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import stock.service.LineMessageService;
import stock.service.StockService;
import stock.vo.lineMessage.Event;
import stock.vo.lineMessage.EventWrapper;

@RestController
@RequestMapping("/api")
@LineMessageHandler
public class StockBotController {

	StockService stockService = new StockService();
	LineMessageService lineMessageService = new LineMessageService();


	@PostMapping("/stock/callback")
	public void handleLineCallBack(@RequestBody EventWrapper events) {
		for (Event event : events.getEvents()) {
			switch (event.getType()) {
			// 當event為message時進入此case執行，其他event(如follow、unfollow、leave等)的case在此省略，您可自己增加
			case "message":
				System.out.print("This is a message event!");
				switch (event.getMessage().getType()) {
				// 當message type為text時，進入此case執行，目前子是將使用者傳來的文字訊息在其前加上echo字串後，回傳給使用者
				case "text":
					lineMessageService.SendResponseMessages(event.getReplyToken(), event.getMessage().getText());
					// event.getSource().getRoomId();
					System.out.println("This is a text message. It's replytoken is " + event.getReplyToken());

					break;
				// 當message type為image時，進入此case執行，
				case "image":
					System.out.println("This is a image message. It's replytoken is " + event.getReplyToken());
					break;
				// 當message type為audio時，進入此case執行，
				case "audio":
					System.out.println("This is a audio message. It's replytoken is " + event.getReplyToken());
					break;
				// 當message type為video時，進入此case執行，
				case "video":
					System.out.println("This is a video message. It's replytoken is " + event.getReplyToken());
					break;
				// 當message type為file時，進入此case執行，
				case "file":
					System.out.println("This is a file message. It's replytoken is " + event.getReplyToken());
					break;
				// 當message type為sticker時，進入此case執行，
				case "sticker":
					System.out.println("This is a sticker message. It's replytoken is " + event.getReplyToken());
					break;
				// 當message type為location時，進入此case執行，
				case "location":
					System.out.println("This is a location message. It's replytoken is " + event.getReplyToken());
					break;
				}

				break;
			}
		}
	}

}
