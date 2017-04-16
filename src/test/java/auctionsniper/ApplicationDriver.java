package auctionsniper;

import com.objogate.wl.robot.RoboticAutomaton;
import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JLabelDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;

import java.time.Duration;

import static auctionsniper.ui.MainWindow.MAIN_WINDOW_NAME;
import static auctionsniper.ui.MainWindow.SNIPER_STATUS_NAME;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by holi on 4/17/17.
 */
@SuppressWarnings("unchecked")
public class ApplicationDriver extends JFrameDriver {

    public ApplicationDriver(Duration timeout) {
        super(new GesturePerformer(new RoboticAutomaton()),
                topLevelFrame(named(MAIN_WINDOW_NAME), showingOnScreen()),
                new AWTEventQueueProber(timeout.toMillis(), 100));
    }

    public void showsSniperStatus(String statusText) {
        new JLabelDriver(this, named(SNIPER_STATUS_NAME)).
                hasText(equalTo(statusText));
    }
}
