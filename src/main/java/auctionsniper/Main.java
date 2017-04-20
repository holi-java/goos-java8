package auctionsniper;

import auctionsniper.ui.MainWindow;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

import static auctionsniper.ui.MainWindow.STATUS_BIDDING;
import static auctionsniper.ui.MainWindow.STATUS_LOST;
import static java.lang.String.format;

/**
 * Created by holi on 4/17/17.
 */
public class Main implements AuctionEventListener {
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_RESOURCE = "Auction";
    public static final String AUCTION_ID_FORMAT = format("%s@%%s/%s", ITEM_ID_AS_LOGIN, AUCTION_RESOURCE);
    public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
    public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";

    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;
    private static final int ARG_ITEM_ID = 3;
    private MainWindow ui;
    private Chat notToBeGCd;

    public static void main(String... args) throws Exception {
        Main main = new Main();
        main.joinAuction(connection(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]),
                args[ARG_ITEM_ID]);
    }

    private static String auctionId(String itemId, XMPPConnection connection) {
        return format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
    }

    private static XMPPConnection connection(String hostname, String username, String password) throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
        return connection;
    }

    public Main() throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(this::startUserInterface);
    }

    private void startUserInterface() {
        ui = new MainWindow();
    }

    private void joinAuction(XMPPConnection connection, String itemId) throws XMPPException {
        ui.whenClosed(connection::disconnect);
        Chat chat = connection.getChatManager().createChat(auctionId(itemId, connection), new AuctionMessageTranslator(this));
        notToBeGCd = chat;
        chat.sendMessage(JOIN_COMMAND_FORMAT);
    }


    @Override
    public void auctionClosed() {
        SwingUtilities.invokeLater(() -> ui.showStatus(STATUS_LOST));
    }

    @Override
    public void currentPrice(int price, int increment) {
        
    }
}
