package auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

/**
 * Created by holi on 4/18/17.
 */
public class AuctionMessageTranslator implements MessageListener {
    private AuctionEventListener listener;

    public AuctionMessageTranslator(AuctionEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void processMessage(Chat chat, Message message) {
        Map<String, String> event = unpackEventFrom(message);
        String type = event.get("Event");
        if ("CLOSE".equals(type)) {
            listener.auctionClosed();
        } else if ("PRICE".equals(type)) {
            listener.currentPrice(Integer.parseInt(event.get("CurrentPrice"))
                    , Integer.parseInt(event.get("Increment")));
        }
    }

    private Map<String, String> unpackEventFrom(Message message) {
        return fields(message.getBody()).collect(toMap(this::fieldName, this::fieldValue));
    }

    private Stream<String[]> fields(String body) {
        return Arrays.stream(body.split(";")).map(this::field);
    }

    private String[] field(String it) {
        return it.split(":");
    }

    private String fieldName(String[] field) {
        return field[0].trim();
    }

    private String fieldValue(String[] field) {
        return field[1].trim();
    }

}
