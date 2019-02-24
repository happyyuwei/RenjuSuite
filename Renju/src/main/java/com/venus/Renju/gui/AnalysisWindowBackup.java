/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venus.Renju.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.venus.Renju.core.*;

/**
 *
 * @author happy
 */
public class AnalysisWindowBackup extends Window {

    //三种模式
    final public static String MODE_AI = "人机模式";
    final public static String MODE_FREE = "自由模式";
    final public static String MODE_VIDEO = "录像";

    //mode
    final private String mode;
    //board panel
    final private BoardPanel board_panel;
    //player choose box
    final private JComboBox player_box;
    //analys panel
    final private JTextArea analys_area;
    //analys button
    final private JButton analys_button;

    //frequency analys result format
    final private String freq_format = "%s:\n单边活2：%d, 双边活2：%d\n单边活3：%d, 双边活3：%d\n单边活4：%d, 双边活4：%d\n五连：%d\n";

    //AI模式 实例区域
    //search engine
    private SearchEngine search_engine;
    //value model
    private SimpleValueModel model;
    //round
    private int round = 1;
    //player id
    private byte player_id;
    //ai id
    private byte ai_id;

    /**
     * constructor.
     *
     * @param mode
     */
    public AnalysisWindowBackup(String mode) {
        super("走棋分析-" + mode);
        //save mode
        this.mode = mode;
        //create board panel, theme and grid number
        //open redo
        this.board_panel = new BoardPanel(15, "blue", true, true);
        //player choose box
        this.player_box = new JComboBox(new String[]{Referee.BLACK_PLAYER, Referee.WHITE_PLAYER});
        this.player_box.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 30));
        //analys area
        this.analys_area = new JTextArea(5, 30);
        this.analys_area.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
        this.analys_area.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 20));
        //analys button
        this.analys_button = new JButton("分析");
        this.analys_button.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 30));
        //add to background
        super.add(this.board_panel, BorderLayout.CENTER);
        super.add(this.player_box, BorderLayout.NORTH);
        super.add(new JScrollPane(this.analys_area), BorderLayout.WEST);
        super.add(this.analys_button, BorderLayout.SOUTH);
        super.setVisible(true);
        //add player change listener
        this.player_box.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                this.board_panel.setPlayerId(String.valueOf(e.getItem()));
            }
        });
        //add analys listener
        this.analys_button.addActionListener((ActionEvent e) -> {
            //analys
            this.analys();
        });

    }

    /**
     * initial windows.
     */
    public void init() {
        //initial board
        this.board_panel.updateBoard();
        //initial player is black player
        this.board_panel.setPlayerId(Referee.BLACK_PLAYER);
        //启动游戏
        if (this.mode.equals(AnalysisWindowBackup.MODE_AI)) {
            //model
            this.model = new SimpleValueModel();
            //search engine
            this.search_engine = new SearchEngine(Referee.BLACK_PLAYER, 2, this.board_panel.referee, this.model);
            //add lsitener
            this.addAiProcess();
            //start
            this.startGame();
        }
    }

    /**
     *
     */
    public void analys() {

    }

    /**
     * 分析棋盘频率.
     */
    public void analys_frequency() {
        //analys black frequency
        int[] black_freq = model.envaluate_frequency(this.board_panel.referee.getBoard(), this.board_panel.referee.getBlack_id());
        //analys white frequency
        int[] white_freq = model.envaluate_frequency(this.board_panel.referee.getBoard(), this.board_panel.referee.getWhite_id());
        //show
        //black
        this.analys_area.append(String.format(this.freq_format, "黑棋：", black_freq[0], black_freq[1], black_freq[2], black_freq[3], black_freq[4], black_freq[5], black_freq[6]));
        this.analys_area.append("\n\n");
        //white
        this.analys_area.append(String.format(this.freq_format, "白棋：", white_freq[0], white_freq[1], white_freq[2], white_freq[3], white_freq[4], white_freq[5], white_freq[6]));
    }

    /**
     * 启动游戏.
     */
    public void startGame() {
        //第一轮
        this.round = 1;
        //重置棋盘
        this.board_panel.referee.restart();
        //刷新
        this.board_panel.fresh();
        //显示
        this.analys_area.setText(String.format("第 %d 轮", this.round));
        //随机选择谁先走
        double rand = Math.random();
        if (rand > 0.5) {
            //玩家是黑棋
            this.player_id = this.board_panel.referee.getBlack_id();
            //ai 白棋
            this.ai_id = this.board_panel.referee.getWhite_id();
            //show
            JOptionPane.showMessageDialog(null, "玩家先走", "走棋", JOptionPane.INFORMATION_MESSAGE);
        } else {
            //玩家是白棋
            this.player_id = this.board_panel.referee.getBlack_id();
            //ai 黑棋
            this.ai_id = this.board_panel.referee.getWhite_id();
            //show
            JOptionPane.showMessageDialog(null, "AI先走", "走棋", JOptionPane.INFORMATION_MESSAGE);
        }
        //如果黑棋先走
        if (this.ai_id == this.board_panel.referee.getBlack_id()) {
            this.aiTurn();
        }
    }

    /**
     * AI 走棋.
     */
    public void aiTurn() {
        //搜索
        Point ai_p = this.search_engine.search();
        //走棋
        this.board_panel.referee.turn(ai_p, this.board_panel.referee.getWhite_id());
        //刷新
        this.board_panel.fresh();
    }

    /**
     * 添加AI 走棋过程.
     */
    private void addAiProcess() {
        this.board_panel.setListener(() -> {
            //judge win or not
            if(this.judgeWin()==true){
                return;
            }
            this.analys_area.setText(String.format("第%d轮\n\n", this.round) + "玩家已走棋...\n\n");
            //analysis player board frequency
            this.analys_frequency();
            //ai turn
            this.analys_area.append("\n\nAI正在分析...\n\n");
            //如果ai先走,一轮结束
            if (this.ai_id == this.board_panel.referee.getBlack_id()) {
                this.round = this.round + 1;
            }
            //ai turn
            this.aiTurn();
            //judge win or not
            if(this.judgeWin()==true){
                return;
            }
            this.analys_area.setText(String.format("第%d轮\n\n", this.round) + "AI已走棋...\n\n");
            //analysis ai board frequency
            this.analys_frequency();
            //如果玩家先走,一轮结束
            if (this.player_id == this.board_panel.referee.getBlack_id()) {
                this.round = this.round + 1;
            }
        });
    }

    /**
     * 判断当前棋盘是否赢
     *
     * @param player_id
     * @return
     */
    private boolean judgeWin() {
        int max_len = Referee.maxSub(this.board_panel.referee.getBoard(), this.player_id);
        if (max_len >= 5) {
            JOptionPane.showMessageDialog(this, "你赢了", "", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } else {
            max_len = Referee.maxSub(this.board_panel.referee.getBoard(), this.ai_id);
            if (max_len >= 5) {
                JOptionPane.showMessageDialog(this, "你输了", "", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        }
        return false;
    }
}
