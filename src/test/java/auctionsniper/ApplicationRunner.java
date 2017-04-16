package auctionsniper;

import java.time.Duration;

import static auctionsniper.FakeAuctionServer.XMPP_HOSTNAME;
import static auctionsniper.ui.MainWindow.STATUS_JOINING;
import static auctionsniper.ui.MainWindow.STATUS_LOST;

/**
 * Created by holi on 4/17/17.
 */
public class ApplicationRunner {
    private static final String SNIPER_ID = "sniper";
    private static final String SNIPER_PASSWORD = "sniper";
    private ApplicationDriver driver;

    public void startBiddingIn(FakeAuctionServer auction) {
        startApplication(auction);
        driver = new ApplicationDriver(Duration.ofSeconds(1));
        driver.showsSniperStatus(STATUS_JOINING);
    }

    private void startApplication(FakeAuctionServer auction) {
        Thread thread = new Thread(() -> {
            try {
                Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void showsSniperHasLostAuction() {
        driver.showsSniperStatus(STATUS_LOST);
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }
}
