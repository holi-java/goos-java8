package auctionsniper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static auctionsniper.ApplicationRunner.SNIPER_XMPP_ID;

/**
 * Created by holi on 4/16/17.
 */
public class AuctionSniperEndToEndTest {
    private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    private final ApplicationRunner application = new ApplicationRunner();

    @Test
    void sniperJoinsAnAuctionUntilAuctionCloses() throws Throwable {
        auction.startSellingItem();

        application.startBiddingIn(auction);
        auction.hasReceivedJoiningRequestFrom(SNIPER_XMPP_ID);

        auction.announceClosed();
        application.showsSniperHasLostAuction();
    }

    @Test
    void sniperMakesAHigherBidButLoses() throws Throwable {
        auction.startSellingItem();

        application.startBiddingIn(auction);
        auction.hasReceivedJoiningRequestFrom(SNIPER_XMPP_ID);

        auction.reportPrice(1000, 98, "other bidder");

        application.hasShownSniperIsBidding();
        auction.hasReceivedBid(1098, SNIPER_XMPP_ID);

        auction.announceClosed();
        application.showsSniperHasLostAuction();
    }


    @AfterEach
    void stopAuction() throws Throwable {
        auction.stop();
    }

    @AfterEach
    void stopApplication() throws Throwable {
        application.stop();
    }
}
