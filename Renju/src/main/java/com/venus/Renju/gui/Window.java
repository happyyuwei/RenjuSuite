/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venus.Renju.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

/**
 *
 * @author happy
 */
public class Window extends JFrame {

    final private JPanel background;

    /**
     *
     * @param title
     */
    public Window(String title) {
        super(title);
        //get screen size
        Dimension size=Toolkit.getDefaultToolkit().getScreenSize();
        //set size
        super.setSize(size.width,(int)(size.height*0.96));
        //set close operation
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //set location on ceneter
        super.setLocationRelativeTo(null);
        //set threme
        Window.setWindows10Threme();
        super.setLayout(new BorderLayout());
        //set background
        this.background = new JPanel();
        background.setBackground(Color.WHITE);
        super.add(background, BorderLayout.CENTER);
    }

    /**
     *
     * @param component
     * @return
     */
    @Override
    public Component add(Component component, int index) {
        return this.background.add(component, index);
    }

    /**
     *
     */
    public static void setWindows10Threme() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
    
    /**
     * copy image
     * @param img
     * @return 
     */
    public static BufferedImage copyImage(BufferedImage img){
        BufferedImage copy=new BufferedImage(img.getWidth(),img.getHeight(),img.getType());
        Graphics2D g=copy.createGraphics();
        g.drawImage(img, null, null);
        g.dispose();
        return copy;
    }

}
