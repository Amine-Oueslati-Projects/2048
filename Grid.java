package org.cis1200.twentyfortyeight;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;

public class Grid extends JPanel implements KeyListener {
    private final TwentyFortyEight tfe;

    private final JLabel scoreLabel;
    private final JLabel highestScoreLabel;
    private final JLabel statusLabel;

    private static final int BOARD_WIDTH = 450;
    private static final int BOARD_HEIGHT = 493;

    public Grid() {
        tfe = new TwentyFortyEight();

        scoreLabel = new JLabel();
        highestScoreLabel = new JLabel();
        statusLabel = new JLabel();

        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        highestScoreLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new GridLayout(1, 2));
        scorePanel.add(scoreLabel);
        scorePanel.add(highestScoreLabel);

        this.setLayout(new BorderLayout());
        this.add(scorePanel, BorderLayout.NORTH);

        GameGridPanel gameGridPanel = new GameGridPanel(this.tfe);
        this.add(gameGridPanel, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel();
        statusPanel.add(statusLabel);
        this.add(statusPanel, BorderLayout.SOUTH);

        setFocusable(true);
        addKeyListener(this);

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                tfe.playTurn(TwentyFortyEight.Direction.UP);
                repaint();
                break;
            case KeyEvent.VK_DOWN:
                tfe.playTurn(TwentyFortyEight.Direction.DOWN);
                repaint();
                break;
            case KeyEvent.VK_LEFT:
                tfe.playTurn(TwentyFortyEight.Direction.LEFT);
                repaint();
                break;
            case KeyEvent.VK_RIGHT:
                tfe.playTurn(TwentyFortyEight.Direction.RIGHT);
                repaint();
                break;
            default:
                break;

        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        return;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        return;
    }

    public void reset(boolean first) {
        tfe.reset(first);
        repaint();

        requestFocusInWindow();
    }

    public void undo() {
        tfe.undo();
        repaint();

        requestFocusInWindow();
    }

    private void updateScoreDisplay() {
        scoreLabel.setText("Score: " + tfe.getScore());
        highestScoreLabel.setText("Highest Score: " + tfe.getHighestScore());
    }

    private void updateStatusDisplay() {
        TwentyFortyEight.GameState gameState = tfe.getState();
        switch (gameState) {
            case WON:
                statusLabel.setText("You Won!");
                break;
            case LOST:
                statusLabel.setText("Game Over!");
                break;
            default:
                statusLabel.setText("Game Ongoing...");
                break;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        updateScoreDisplay();
        updateStatusDisplay();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    }
}

class GameGridPanel extends JPanel {
    private final TwentyFortyEight tfe;
    private static final int BOARD_WIDTH = 450;
    private static final int BOARD_HEIGHT = 450;

    public GameGridPanel(TwentyFortyEight tfe) {
        this.tfe = tfe;
    }

    private Color getTileColor(int value) {
        switch (value) {
            case 2:
                return new Color(238, 228, 218);
            case 4:
                return new Color(237, 224, 200);
            case 8:
                return new Color(242, 177, 121);
            case 16:
                return new Color(245, 149, 99);
            case 32:
                return new Color(246, 124, 95);
            case 64:
                return new Color(246, 94, 59);
            case 128:
                return new Color(237, 207, 114);
            case 256:
                return new Color(237, 204, 97);
            case 512:
                return new Color(237, 200, 80);
            case 1024:
                return new Color(237, 197, 63);
            case 2048:
                return new Color(237, 194, 46);
            default:
                return new Color(205, 193, 180);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int tileSize = 100;
        int tileMargin = 10;

        g.setColor(new Color(187, 173, 160));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int value = tfe.getValue(i, j);
                int x = j * (tileMargin + tileSize) + tileMargin;
                int y = i * (tileMargin + tileSize) + tileMargin;

                g.setColor(getTileColor(value));
                g.fillRect(x, y, tileSize, tileSize);

                if (value != 0) {
                    String s = String.valueOf(value);
                    FontMetrics fm = g.getFontMetrics();
                    int asc = fm.getAscent();
                    int dec = fm.getDescent();

                    int xString = x + (tileSize - fm.stringWidth(s)) / 2;
                    int yString = y + (asc + (tileSize - (asc + dec)) / 2);

                    g.setColor(Color.black);
                    g.drawString(s, xString, yString);
                }
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    }
}