package org.cis1200.twentyfortyeight;

import javax.swing.*;
import java.awt.*;

import javax.swing.JOptionPane;

public class RunTwentyFortyEight implements Runnable {
    public void run() {
        String instructionsMessage = "Welcome to 2048!\n\n" +
                "Instructions:\n" +
                "- Use the arrow keys to move the tiles.\n" +
                "- Combine tiles with the same value to reach 2048.\n" +
                "- Have fun and try to achieve the highest score!";

        JOptionPane.showMessageDialog(
                null, instructionsMessage,
                "2048 Instructions", JOptionPane.INFORMATION_MESSAGE
        );

        final JFrame frame = new JFrame("2048");
        frame.setLocation(500, 250);

        final Grid grid = new Grid();
        frame.add(grid, BorderLayout.CENTER);

        final JPanel control_panel = new JPanel();
        frame.add(control_panel, BorderLayout.NORTH);

        final JButton reset = new JButton("Reset");
        reset.addActionListener(e -> {
            grid.reset(false);
        });
        control_panel.add(reset);

        final JButton undo = new JButton("Undo");
        undo.addActionListener(e -> grid.undo());
        control_panel.add(undo);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        grid.reset(true);
    }
}
