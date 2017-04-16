package auctionsniper;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static auctionsniper.Main.AUCTION_RESOURCE;
import static auctionsniper.Main.ITEM_ID_AS_LOGIN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

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
        return String.format(ITEM_ID_AS_LOGIN, getItemId());
    }

    public void hasReceivedJoiningRequestFromSniper() throws InterruptedException {
        messageListener.receivesAMessage();
    }

    public void announceClosed() throws XMPPException {
        currentChat.sendMessage(new Message());
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

        public void receivesAMessage() throws InterruptedException {
            assertThat("incoming message", messages.poll(1, TimeUnit.SECONDS), is(notNullValue()));
        }
    }
}
