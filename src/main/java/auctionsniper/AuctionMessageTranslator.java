package auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import static auctionsniper.AuctionEventListener.PriceSource.FromOtherBidder;
import static auctionsniper.AuctionEventListener.PriceSource.FromSniper;
import static java.util.stream.Collectors.toMap;

/**
 * Created by holi on 4/18/17.
 */
public class AuctionMessageTranslator implements MessageListener {
    public static final String CLOSE_EVENT = "CLOSE";
    public static final String CURRENT_PRICE_EVENT = "PRICE";
    private String sniperId;
    private AuctionEventListener listener;

    public AuctionMessageTranslator(String sniperId, AuctionEventListener listener) {
        this.sniperId = sniperId;
        this.listener = listener;
    }

    @Override
    public void processMessage(Chat chat, Message message) {
        AuctionEvent event = AuctionEvent.from(message.getBody());
        String type = event.type();
        if (CLOSE_EVENT.equals(type)) {
            listener.auctionClosed();
        } else if (CURRENT_PRICE_EVENT.equals(type)) {
            listener.currentPrice(event.currentPrice(), event.increment(), event.isFrom(sniperId));
        }
    }

    private static class AuctionEvent {
        private Map<String, String> fields;

        public AuctionEvent(Map<String, String> fields) {
            this.fields = fields;
        }

        private static AuctionEvent from(String body) {
            return new AuctionEvent(fieldsIn(body).collect(toMap(AuctionEvent::fieldName, AuctionEvent::fieldValue)));
        }

        private static Stream<String[]> fieldsIn(String body) {
            return Arrays.stream(body.split(";")).map(AuctionEvent::field);
        }

        private static String[] field(String it) {
            return it.split(":");
        }

        private static String fieldName(String[] field) {
            return field[0].trim();
        }

        private static String fieldValue(String[] field) {
            return field[1].trim();
        }

        public String type() {
            return get("Event");
        }

        public int currentPrice() {
            return getInt("CurrentPrice");
        }

        public int increment() {
            return getInt("Increment");
        }

        private String bidder() {
            return get("Bidder");
        }

        private AuctionEventListener.PriceSource isFrom(String sniperId) {
            return sniperId.equals(bidder()) ? FromSniper : FromOtherBidder;
        }

        private int getInt(String fieldName) {
            return Integer.parseInt(get(fieldName));
        }

        private String get(String fieldName) {
            return fields.get(fieldName);
        }
    }
}
