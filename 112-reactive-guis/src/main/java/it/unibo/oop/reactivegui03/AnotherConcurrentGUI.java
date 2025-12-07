package it.unibo.oop.reactivegui03;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unibo.oop.JFrameUtil;

import java.io.Serial;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Third experiment with reactive gui.
 */
public final class AnotherConcurrentGUI extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(AnotherConcurrentGUI.class);
    private final JLabel label = new JLabel("0");
    private final transient Agent agent = new Agent();
    private final JButton up = new JButton("Up");
    private final JButton down = new JButton("down");
    private final JButton stop = new JButton("Stop");

    /**
     * Constructor.
     */
    public AnotherConcurrentGUI() {
        JFrameUtil.dimensionJFrame(this);
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final ExecutorService interruptexecutor = Executors.newSingleThreadExecutor();

        panel.add(label);
        panel.add(up);
        panel.add(stop);
        panel.add(down);

        this.add(panel);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);

        executor.execute(agent);
        interruptexecutor.execute(new InterruptAgent());

        stop.addActionListener(l -> {
            agent.stopCounter();
            stop.setEnabled(false);
            up.setEnabled(false);
            down.setEnabled(false);
        });
        up.addActionListener(l -> agent.setPositiveSign(true));
        down.addActionListener(l -> agent.setPositiveSign(false));
    }

    private final class Agent implements Runnable {
        private int counter;
        private volatile boolean positive = true;
        private volatile boolean stop;

        @Override
        public void run() {
            try {
                while (!stop) {
                    counter = positive ? counter + 1 : counter - 1;
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.label.setText(String.valueOf(counter)));
                    Thread.sleep(100);
                }
            } catch (InterruptedException | InvocationTargetException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        void stopCounter() {
            this.stop = true;
        }
 
        void setPositiveSign(final boolean isPositive) {
            this.positive = isPositive;
        }
    }

    private final class InterruptAgent implements Runnable {
        private static final int WAIT_TIME = 10_000;

        @Override
        public void run() {
            try {
                Thread.sleep(WAIT_TIME);
                SwingUtilities.invokeAndWait(() -> {
                    AnotherConcurrentGUI.this.down.setEnabled(false);
                    AnotherConcurrentGUI.this.up.setEnabled(false);
                    AnotherConcurrentGUI.this.stop.setEnabled(false);
                });

                AnotherConcurrentGUI.this.agent.stopCounter();
            } catch (final InterruptedException | InvocationTargetException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

    }
}
