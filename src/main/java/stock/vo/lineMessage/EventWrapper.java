package stock.vo.lineMessage;

import java.util.List;

import lombok.Data;

@Data
public class EventWrapper {
	private List<Event> events;

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}
}