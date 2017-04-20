package auctionsniper.ui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by holi on 4/17/17.
 */
public class MainWindow extends JFrame {
    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static final String SNIPER_STATUS_NAME = "Sniper Status";
    public static final String STATUS_JOINING = "JOINING";
    public static final String STATUS_LOST = "LOST";
    public static final String STATUS_BIDDING = "BIDDING";
    private JLabel sniperStatus = createSniperStatus(STATUS_JOINING);

    public MainWindow() throws HeadlessException {
        super("Sniper Application");
        setName(MAIN_WINDOW_NAME);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        add(sniperStatus);
        pack();
        setVisible(true);
    }

    private JLabel createSniperStatus(String initialText) {
        JLabel it = new JLabel();
        it.setText(initialText);
        it.setName(SNIPER_STATUS_NAME);
        it.setBorder(new LineBorder(Color.BLACK));
        return it;
    }

    public void showStatus(String statusText) {
        sniperStatus.setText(statusText);
    }

    public void whenClosed(Runnable disconnect) {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                disconnect.run();
            }
        });
    }
}
