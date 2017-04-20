package auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Created by holi on 4/18/17.
 */
@ExtendWith(JMockExtension.class)
public class AuctionMessageTranslatorTest {
    private final Mockery context = new Mockery();
    private static final Chat UNUSED_CHAT = null;

    private final AuctionEventListener listener = context.mock(AuctionEventListener.class);
    private final AuctionMessageTranslator translator = new AuctionMessageTranslator(listener);

    @Test
    void notifiesAuctionClosedWhenCloseMessageReceived() throws Throwable {
        context.checking(new Expectations() {{
            oneOf(listener).auctionClosed();
        }});

        translator.processMessage(UNUSED_CHAT, message("SOLVersion: 1.1; Event:CLOSE;"));
    }


    @Test
    void notifiesBidDetailsWhenCurrentPriceMessageReceived() throws Throwable {
        context.checking(new Expectations() {{
            oneOf(listener).currentPrice(123,45);
        }});

        translator.processMessage(UNUSED_CHAT, message("SOLVersion: 1.1; Event:PRICE; CurrentPrice:123; Increment:45; Bidder: Someone Else;"));
    }

    private Message message(String body) {
        Message it = new Message();
        it.setBody(body);
        return it;
    }
}
