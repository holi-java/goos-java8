package auctionsniper;

import org.hamcrest.Matcher;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static auctionsniper.Main.*;
import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by holi on 4/17/17.
 */
public class FakeAuctionServer {
    public static final String XMPP_HOSTNAME = "localhost";
    private static final String AUCTION_PASSWORD = "auction";
    private final SingleMessageListener messageListener = new SingleMessageListener();
    private String itemId;
    private Chat currentChat;
    private final XMPPConnection connection;

    public FakeAuctionServer(String itemId) {
        this.itemId = itemId;
        connection = new XMPPConnection(XMPP_HOSTNAME);
    }

    public String getItemId() {
        return itemId;
    }

    public void startSellingItem() throws XMPPException {
        connection.connect();
        connection.login(auctionId(), AUCTION_PASSWORD, AUCTION_RESOURCE);
        connection.getChatManager().addChatListener(joinTheChat());
    }

    private ChatManagerListener joinTheChat() {
        return (chat, createdLocally) -> {
            currentChat = chat;
            chat.addMessageListener(messageListener);
        };
    }

    private String auctionId() {
        return format(ITEM_ID_AS_LOGIN, getItemId());
    }

    public void reportPrice(int price, int increment, String bidder) throws XMPPException {
        currentChat.sendMessage(format("SOLVersion: 1.1; Event: PRICE; CurrentPrice: %d; Increment: %d; Bidder: %s;", price, increment, bidder));
    }

    public void hasReceivedJoiningRequestFrom(String sniperId) throws InterruptedException {
        receivesAMessageMatching(sniperId, equalTo(JOIN_COMMAND_FORMAT));
    }

    public void hasReceivedBid(int bid, String sniperId) throws InterruptedException {
        receivesAMessageMatching(sniperId, equalTo(format(BID_COMMAND_FORMAT, bid)));
    }

    private void receivesAMessageMatching(String sniperId, Matcher<String> messageBodyMatcher) throws InterruptedException {
        messageListener.receivesAMessage(messageBodyMatcher);
        assertThat(currentChat.getParticipant(), equalTo(sniperId));
    }

    public void announceClosed() throws XMPPException {
        currentChat.sendMessage("SOLVersion: 1.1; Event: CLOSE;");
    }

    public void stop() {
        connection.disconnect();
    }

    private static class SingleMessageListener implements MessageListener {
        private BlockingQueue<Message> messages = new ArrayBlockingQueue<>(1);

        @Override
        public void processMessage(Chat chat, Message message) {
            messages.add(message);
        }

        public void receivesAMessage(Matcher<String> messageBodyMatcher) throws InterruptedException {
            Message received = messages.poll(1, TimeUnit.SECONDS);
            assertThat("message", received, is(notNullValue()));
            assertThat("message body", received, hasProperty("body", messageBodyMatcher));
        }
    }
}
