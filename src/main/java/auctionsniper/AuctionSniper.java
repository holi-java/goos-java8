package auctionsniper;

import static auctionsniper.AuctionEventListener.PriceSource.FromSniper;

/**
 * Created by holi on 4/20/17.
 */
public class AuctionSniper implements AuctionEventListener {
    private Auction auction;
    private SniperListener listener;
    private boolean isWinning = false;

    public AuctionSniper(Auction auction, SniperListener listener) {
        this.auction = auction;
        this.listener = listener;
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource source) {
        isWinning = source == FromSniper;
        if (isWinning) {
            listener.sniperWinning();
        } else {
            auction.bid(price + increment);
            listener.sniperBidding();
        }
    }

    @Override
    public void auctionClosed() {
        if (isWinning) {
            listener.sniperWon();
        } else {
            listener.sniperLost();
        }
    }
}
