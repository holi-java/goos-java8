package auctionsniper;

import java.util.EventListener;

/**
 * Created by holi on 4/18/17.
 */
public interface AuctionEventListener extends EventListener {
    void auctionClosed();

    void currentPrice(int price, int increment);
}
