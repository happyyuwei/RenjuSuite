/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venus.Renju.gui;

import com.venus.Renju.core.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.*;

/**
 *
 * @author happy
 */
public class BoardPanel extends JPanel {

    //drawable
    final private String drawable = ".\\drawable\\";
    //points
    final private String black_dir;
    final private String white_dir;
    //background
    final private String background_dir;
    //background label
    final private JLabel label;
    //popup menu
    final private JPopupMenu popup_menu;

    //grid num
    final private int grid_num;

    //board referee
    final public Referee referee;

    //current grid width
    private int grid_width;
    //current left top point
    private Point left_top_point;
    //background board
    private BufferedImage current_background;
    //point
    private Image black_point;
    private Image white_point;
    //point whidth
    private int point_width;
    //player
    private byte player_id = 0;

    //listener
    private BoardPanelListener listener;

    /**
     *
     * @param grid_num
     * @param theme
     * @param redo
     * @param open_popup_menu
     */
    public BoardPanel(int grid_num, String theme, boolean redo, boolean open_popup_menu) {
        this(new Referee(grid_num), theme, redo, open_popup_menu);
    }

    /**
     * constrcutor
     *
     * @param referee
     * @param theme
     * @param redo
     * @param open_popup_menu
     */
    public BoardPanel(Referee referee, String theme, boolean redo, boolean open_popup_menu) {
        super();
        //border layout
        super.setLayout(new BorderLayout());
        super.setVisible(true);
        //threme dir
        this.background_dir = this.drawable + "theme\\" + theme + "\\background.png";
        this.black_dir = this.drawable + "theme\\" + theme + "\\black.png";
        this.white_dir = this.drawable + "theme\\" + theme + "\\white.png";
        this.label = new JLabel("", JLabel.CENTER);
//        JScrollPane scroll=new JScrollPane(this.label);
        super.add(this.label, BorderLayout.CENTER);
        //background
        super.setBackground(Color.WHITE);
        //save board width
        this.grid_num = referee.getBoardWidth();
        //initial referee
        this.referee = referee;
        //set redo
        this.referee.setBackup(redo);
        //set popup menu
        if (open_popup_menu == true) {
            Font menu_font = new Font(Font.DIALOG_INPUT, Font.BOLD, 20);
            this.popup_menu = new JPopupMenu();
            //redo menu
            JMenuItem redo_item = new JMenuItem("撤销");
            redo_item.setFont(menu_font);
            this.popup_menu.add(redo_item);
            //add menu_item listener
            redo_item.addActionListener((ActionEvent e) -> {
                this.redo();
            });
            //restart menu
            JMenuItem restart_item = new JMenuItem("重置");
            restart_item.setFont(menu_font);
            this.popup_menu.add(restart_item);
            //add menu_item listener
            restart_item.addActionListener((ActionEvent e) -> {
                this.restart();
            });
            JMenuItem save_item = new JMenuItem("保存");
            save_item.setFont(menu_font);
            this.popup_menu.add(save_item);
            //add menu_item listener
            save_item.addActionListener((ActionEvent e) -> {
                this.save();
            });
        } else {
            this.popup_menu = null;
        }
        //add listener
        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //鼠标左键
                if (e.getButton() == 1) {
                    //走棋，刷新棋盘
                    if (BoardPanel.this.turnNextStep(e) == true) {
                        //只有走棋成功，才会调用外边监听器
                        //more work in a new thread
                        if (BoardPanel.this.listener != null) {
                            new Thread(() -> {
                                //more work
                                BoardPanel.this.listener.after();
                            }).start();
                        }
                    }
                } //鼠标右键
                else if (e.getButton() == 3) {
                    BoardPanel.this.popup_menu.show(BoardPanel.this, e.getX(), e.getY());
                }
            }
        });
        //panel change listener
        super.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                //当容器大小发生变化时，刷新棋盘尺寸
                updateBoard();
            }
        });
    }

    /**
     * 刷新棋盘尺寸,棋盘大小以容器大小宽度为基准.
     */
    public void updateBoard() {
        int panel_width = super.getHeight();
        updateBoard(panel_width);
    }

    /**
     * 以指定大小刷新棋盘尺寸
     *
     * @param width
     */
    public void updateBoard(int width) {
        try {
            //load background
            BufferedImage background = ImageIO.read(new File(this.background_dir));
            //get graphics
            Graphics2D g = background.createGraphics();
            g.setColor(Color.BLACK);
            //get width
            int grid_width = background.getWidth() / (grid_num + 1);
            //draw board
            g.setStroke(new BasicStroke(20.0f));
            g.drawRect(grid_width, grid_width, background.getWidth() - 2 * grid_width, background.getHeight() - 2 * grid_width);
            //draw lines
            g.setStroke(new BasicStroke(5.0f));
            //start height
            int height = 2 * grid_width;
            for (int i = 0; i < grid_num - 1; i++) {
                g.drawLine(grid_width, height, background.getWidth() - grid_width, height);
                g.drawLine(height, grid_width, height, background.getHeight() - grid_width);
                height = height + grid_width;
            }
            g.dispose();
            //change background size
            this.current_background = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB);
            Graphics2D g_current = this.current_background.createGraphics();
            g_current.drawImage(background.getScaledInstance(width, width, Image.SCALE_SMOOTH), null, null);
            g_current.dispose();
            //set background label
            this.label.setIcon(new ImageIcon(Window.copyImage(this.current_background)));
            this.grid_width = width / (grid_num + 1);
            //update left top corner
            this.left_top_point = new Point(super.getWidth() / 2 - width / 2 + this.grid_width, super.getHeight() / 2 - width / 2 + this.grid_width);
            this.point_width = (int) (this.grid_width * 0.8);
            //load points
            this.black_point = ImageIO.read(new File(this.black_dir)).getScaledInstance(point_width, point_width, Image.SCALE_SMOOTH);
            this.white_point = ImageIO.read(new File(this.white_dir)).getScaledInstance(point_width, point_width, Image.SCALE_SMOOTH);
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    /**
     *
     * @param player
     */
    public void setPlayerId(String player) {
        if (player.equals(Referee.BLACK_PLAYER)) {
            this.player_id = referee.getBlack_id();
        } else {
            this.player_id = referee.getWhite_id();
        }
    }

    /**
     *
     * @param frame
     * @param point
     * @param row
     * @param col
     */
    public void draw(BufferedImage frame, Image point, int row, int col) {
        //swicth row and column to pixel
        int x = col * this.grid_width;
        int y = row * this.grid_width;
        //get background graphic
        Graphics2D g = frame.createGraphics();
        //矫正
        x = x + this.grid_width - this.point_width / 2;
        y = y + this.grid_width - this.point_width / 2;
        g.drawImage(point, x, y, null);
    }

    /**
     *
     */
    public void redo() {
        //invoke core
        this.referee.redo();
        //fresh board
        this.fresh();
    }

    /**
     *
     */
    public void restart() {
        //incoke core
        this.referee.restart();
        this.fresh();
    }

    /**
     *
     */
    public void fresh() {
        //get board, draw the board
        byte[][] board = this.referee.getBoard();
        //get copy of background
        BufferedImage frame = Window.copyImage(this.current_background);
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == this.referee.getBlack_id()) {
                    //draw black
                    this.draw(frame, this.black_point, i, j);
                } else if (board[i][j] == this.referee.getWhite_id()) {
                    //draw white
                    this.draw(frame, this.white_point, i, j);
                }
            }
        }
        //update
        this.label.setIcon(new ImageIcon(frame));
        super.updateUI();
    }

    /**
     * 走棋成功返回true, 非法走棋，返回false
     *
     * @param e
     * @return 
     */
    public boolean turnNextStep(MouseEvent e) {
        //if the player id is set
        if (this.player_id != 0) {
            int x = e.getX() - this.left_top_point.x;
            int y = e.getY() - this.left_top_point.y;
            //swicth x and y to row and column
            int row = (y + this.grid_width / 2) / this.grid_width;
            int col = (x + this.grid_width / 2) / this.grid_width;
            //if the row and column is more than the board, do nothing
            if (row >= 0 && col >= 0 && row < this.referee.getBoardWidth() && col < this.referee.getBoardWidth()) {
                //save in board
                int max_num = this.referee.turn(new Point(row, col), this.player_id);
                //非法走棋
                if (max_num == -1) {
                    return false;
                }
                //update board
                this.fresh();
                return true;
            }
        }
        //else do nothing
        return false;
    }

    public void save() {
        this.referee.saveBackup(".\\record\\" + System.currentTimeMillis() + ".txt");
    }

    /**
     * @param listener the listener to set
     */
    public void setListener(BoardPanelListener listener) {
        this.listener = listener;
    }

}
