package com.github.marcostulio;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.github.marcostulio.PaintFrame.Direction;

import net.miginfocom.swing.MigLayout;

public class Main {

    private static Thread game;

    public static void main(String[] args) {
        final JLabel lblScore = new JLabel();
        final JLabel lblSpeed = new JLabel();
        
        final JFrame mainFrame = new JFrame("Snake");
        final PaintFrame paintFrame = new PaintFrame(lblScore, lblSpeed);
        
        game = new Thread(paintFrame);

        final JButton btnPlay = new JButton("Play");
        btnPlay.setFocusable(false);
        btnPlay.addActionListener( e -> {
            if (game.getState() == Thread.State.TERMINATED) {
                paintFrame.newGame();
                game = new Thread(paintFrame);
            }

            if (!game.isAlive()) game.start();
        });

        mainFrame.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                switch( e.getKeyCode() ){
                    case KeyEvent.VK_UP: case KeyEvent.VK_W:
                        paintFrame.direction = Direction.UP; break;
                        
                    case KeyEvent.VK_DOWN: case KeyEvent.VK_S: 
                        paintFrame.direction = Direction.DOWN; break;

                    case KeyEvent.VK_RIGHT: case KeyEvent.VK_D:
                        paintFrame.direction = Direction.RIGHT; break;

                    case KeyEvent.VK_LEFT: case KeyEvent.VK_A: 
                        paintFrame.direction = Direction.LEFT; break;                    
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
