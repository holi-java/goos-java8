package auctionsniper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Created by holi on 4/16/17.
 */
public class AuctionSniperEndToEndTest {
    private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    private final ApplicationRunner application = new ApplicationRunner();

    @Test
    void sniperJoinsAnAuctionUtilItIsClosed() throws Throwable {
        auction.startSellingItem();

        application.startBiddingIn(auction);
        auction.hasReceivedJoiningRequestFromSniper();

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
