package org.example.model;

import javax.swing.*;
import java.awt.*;

public class ShovelButton extends JButton {
    private boolean active = false;

    public ShovelButton() {
//        setIcon(new ImageIcon(getClass().getClassLoader().getResource("Shovel.png")));
        setPreferredSize(new Dimension(60, 60));
        setFocusable(false);
        setOpaque(true);
        setBackground(new Color(Color.TRANSLUCENT));


        addActionListener(e -> {
            active = !active;
            setBorderPainted(active);
        });
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
        setBorderPainted(false);
    }
}

