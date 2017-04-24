package auctionsniper;


import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static auctionsniper.AuctionEventListener.PriceSource.FromOtherBidder;
import static auctionsniper.AuctionEventListener.PriceSource.FromSniper;

/**
 * Created by holi on 4/20/1Test7.
 */
@ExtendWith(JMockExtension.class)
public class AuctionSniperTest {
    private final Mockery context = new Mockery();
    private final States sniperState = context.states("SniperState").startsAs("JOINING");

    private final SniperListener listener = context.mock(SniperListener.class);
    private final Auction auction = context.mock(Auction.class);

    @Test
    public void reportsLostWhenAuctionClosesImmediately() throws Exception {
        context.checking(new Expectations() {{
            atLeast(1).of(listener).sniperLost();
        }});

        sniper.auctionClosed();
    }

    private final AuctionSniper sniper = new AuctionSniper(auction, listener);

    @Test
    void bidsHigherAndReportsBiddingWhenANewPriceComesFromOtherBidder() throws Throwable {
        int currentPrice = 123;
        int increment = 45;

        context.checking(new Expectations() {{
            oneOf(auction).bid(currentPrice + increment);
            atLeast(1).of(listener).sniperBidding();
        }});

        sniper.currentPrice(currentPrice, increment, FromOtherBidder);
    }

    @Test
    void reportsWinningWhenCurrentPriceComesFromSniper() throws Throwable {
        context.checking(new Expectations() {{
            atLeast(1).of(listener).sniperWinning();
        }});

        sniper.currentPrice(123, 45, FromSniper);
    }


    @Test
    public void reportsLostIfAuctionClosesWhenBidding() throws Exception {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(listener).sniperBidding(); then(sniperState.is("bidding"));
            atLeast(1).of(listener).sniperLost(); when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(100, 23, FromOtherBidder);
        sniper.auctionClosed();
    }

    @Test
    public void reportsWonIfAuctionClosesWhenWinning() throws Exception {
        context.checking(new Expectations() {{
            allowing(listener).sniperWinning(); then(sniperState.is("winning"));
            atLeast(1).of(listener).sniperWon(); when(sniperState.is("winning"));
        }});

        sniper.currentPrice(100, 23, FromSniper);
        sniper.auctionClosed();
    }
}
