import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;

public class Main {

    private static Thread game;

    public static void main(String[] args) {
        JLabel lblScore = new JLabel();
        JLabel lblSpeed = new JLabel();
        
        JFrame mainFrame = new JFrame("Snake");
        PaintFrame paintFrame = new PaintFrame(lblScore, lblSpeed);
        
        game = new Thread(paintFrame);

        JButton btnPlay = new JButton("Play");
        btnPlay.setFocusable(false);
        btnPlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (game.getState() == Thread.State.TERMINATED) {
                    paintFrame.newGame();
                    game = new Thread(paintFrame);
                }

                if (!game.isAlive()) {
                    game.start();
                }
            }
        });

        mainFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == e.VK_W) {
                    paintFrame.direction = paintFrame.direction.UP;
                } else if (e.getKeyCode() == e.VK_S) {
                    paintFrame.direction = paintFrame.direction.DOWN;
                } else if (e.getKeyCode() == e.VK_D) {
                    paintFrame.direction = paintFrame.direction.RIGHT;
                } else if (e.getKeyCode() == e.VK_A) {
                    paintFrame.direction = paintFrame.direction.LEFT;
                }
            }
        });

        mainFrame.setLayout(new MigLayout("fill"));
        mainFrame.add(lblScore, "cell 0 0");
        mainFrame.add(lblSpeed, "cell 0 1");
        mainFrame.add(btnPlay, "cell 0 2, top, width 150px");
        mainFrame.add(paintFrame, "cell 1 0, span 0 3");
        mainFrame.setSize(new Dimension(800, 600));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);
        mainFrame.setVisible(true);
    }
}
