package org.cis1200;

import javax.swing.*;

public class Game {
    public static void main(String[] args) {
        Runnable game = new org.cis1200.twentyfortyeight.RunTwentyFortyEight();
        SwingUtilities.invokeLater(game);
    }
}
