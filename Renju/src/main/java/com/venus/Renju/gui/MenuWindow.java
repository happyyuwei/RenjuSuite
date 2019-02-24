/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venus.Renju.gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 *
 * @author happy
 */
public class MenuWindow extends Window{
    
    final private JButton ai_player_button;
    final private JButton free_player_button;
    final private JButton video_button;
    
    /**
     * 
     */
    public MenuWindow(){
        super("菜单");
        super.setSize(500, 600);
        super.setLocationRelativeTo(null);
        
        //
        this.ai_player_button=new JButton("人机对战");
        this.free_player_button=new JButton("自由模式");
        this.video_button=new JButton("视频回放");
        
        //main panel
        JPanel main_panel=new JPanel();
        main_panel.setLayout(new GridLayout(3,1));
        //button
        main_panel.add(this.ai_player_button);
        main_panel.add(this.free_player_button);
        main_panel.add(this.video_button);
        //add
        super.add(main_panel, BorderLayout.CENTER);
        super.setVisible(true);
        //listener
        this.ai_player_button.addActionListener((ActionEvent e) -> {
            AnalysisWindow window=new AnalysisWindow(AnalysisWindow.MODE_AI);
            this.dispose();
            window.init();
        });
        this.free_player_button.addActionListener((ActionEvent e) -> {
            AnalysisWindow window=new AnalysisWindow(AnalysisWindow.MODE_FREE);
            this.dispose();
        });
        this.video_button.addActionListener((ActionEvent e) -> {
            AnalysisWindow window=new AnalysisWindow(AnalysisWindow.MODE_VIDEO);
            this.dispose();
        });
    }
    
}
