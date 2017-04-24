package auctionsniper;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static auctionsniper.FakeAuctionServer.XMPP_HOSTNAME;
import static auctionsniper.Main.AUCTION_RESOURCE;
import static auctionsniper.ui.MainWindow.*;

/**
 * Created by holi on 4/17/17.
 */
public class ApplicationRunner {
    private static final String SNIPER_ID = "sniper";
    private static final String SNIPER_PASSWORD = "sniper";
    public static final String SNIPER_XMPP_ID = String.format("%s@%s/%s", SNIPER_ID, XMPP_HOSTNAME, AUCTION_RESOURCE);
    private ApplicationDriver driver;

    public void startBiddingIn(FakeAuctionServer auction) throws Throwable {
        try {
            startApplication(auction).join();
            driver = new ApplicationDriver(Duration.ofMillis(200));
            driver.showsSniperStatus(STATUS_JOINING);
        } catch (CompletionException e) {
            throw e.getCause();
        }
    }

    private CompletableFuture<Void> startApplication(FakeAuctionServer auction) {
        return new CompletableFuture<Void>() {{
            try {
                Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
                complete(null);
            } catch (Throwable e) {
                completeExceptionally(e);
            }
        }};
    }

    public void showsSniperHasLostAuction() {
        driver.showsSniperStatus(STATUS_LOST);
    }

    public void hasShownSniperIsBidding() {
        driver.showsSniperStatus(STATUS_BIDDING);
    }

    public void hasShownSniperIsWinning() {
        driver.showsSniperStatus(STATUS_WINNING);
    }

    public void showsSniperHasWonAuction() {
        driver.showsSniperStatus(STATUS_WON);
    }

    public void stop() throws Throwable {
        if (driver != null) {
            driver.dispose();
        }
    }
}
