package auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static auctionsniper.AuctionEventListener.PriceSource.FromOtherBidder;
import static auctionsniper.AuctionEventListener.PriceSource.FromSniper;
import static java.lang.String.format;

/**
 * Created by holi on 4/18/17.
 */
@ExtendWith(JMockExtension.class)
public class AuctionMessageTranslatorTest {
    private static final String SNIPER_ID = "SNIPER-ID";
    private final Mockery context = new Mockery();
    private static final Chat UNUSED_CHAT = null;

    private final AuctionEventListener listener = context.mock(AuctionEventListener.class);
    private final AuctionMessageTranslator translator = new AuctionMessageTranslator(SNIPER_ID,listener);

    @Test
    void notifiesAuctionClosedWhenCloseMessageReceived() throws Throwable {
        context.checking(new Expectations() {{
            oneOf(listener).auctionClosed();
        }});

        translator.processMessage(UNUSED_CHAT, message("SOLVersion: 1.1; Event:CLOSE;"));
    }


    @Test
    void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromOtherBidder() throws Throwable {
        context.checking(new Expectations() {{
            oneOf(listener).currentPrice(123, 45, FromOtherBidder);
        }});

        translator.processMessage(UNUSED_CHAT, message("SOLVersion: 1.1; Event:PRICE; CurrentPrice:123; Increment:45; Bidder: Someone Else;"));
    }


    @Test
    void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromSniper() throws Throwable {
        context.checking(new Expectations() {{
            oneOf(listener).currentPrice(123, 45, FromSniper);
        }});

        translator.processMessage(UNUSED_CHAT, message(format("SOLVersion: 1.1; Event:PRICE; CurrentPrice:123; Increment:45; Bidder: %s;", SNIPER_ID)));
    }

    private Message message(String body) {
        Message it = new Message();
        it.setBody(body);
        return it;
    }
}
