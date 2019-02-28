/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venus.Renju.core;

import java.awt.Point;
import java.util.*;

/**
 *
 * @author happy
 */
public class Referee {

    //black and white player
    final public static String BLACK_PLAYER = "black";
    final public static String WHITE_PLAYER = "white";

    //board, o is blank, 1 is white and -1 is black in default
    final private byte[][] board;
    final private byte white_id;
    final private byte black_id;

    //backup list, the first element is the most farther, 
    //the last element is the most near
    //@since 2019.2.16 
    //备份已经移至RefereeSaver类，该类还将管理保存等事宜。
    private RefereeSaver saver;

    /**
     *
     * @param board_width
     */
    public Referee(int board_width) {
        //initial with zero
        this.board = new byte[board_width][board_width];
        //默认白棋代号 1
        this.white_id = 1;
        //默认黑棋代号-1
        this.black_id = -1;
        
    }

    /**
     *
     * @param board_width
     * @param black_id
     * @param white_id
     */
    public Referee(int board_width, byte black_id, byte white_id) {
        //initial with zero
        this.board = new byte[board_width][board_width];
        //customal player id
        this.white_id = white_id;
        this.black_id = black_id;
    }
    

    /**
     * 走棋，返回棋盘上最大连接数量，如果等于五则胜利，如果等于-1，非法走棋。
     *
     * @param point
     * @param player_id
     * @return
     */
    public int turn(Point point, byte player_id) {
        //turn
        if (this.board[point.x][point.y] == 0) {
            //backup
            this.backup();
            this.board[point.x][point.y] = player_id;
            return Referee.maxSub(this.board, player_id);
        } else {
            return -1;
        }
    }
           
     /**
     * 将输入棋盘内的布局复制给当前棋盘，不会直接把输入的应用传给当前棋盘。
     *
     * @param board
     */
    public void setBoard(byte[][] board) {
        //backup
        this.backup();
        //covet
        for (int i = 0; i < this.board.length; i++) {
            System.arraycopy(board[i], 0, this.board[i], 0, this.board[0].length);
        }
    }

    /**
     * 备份当前棋盘布局至历史记录中以便未来需要恢复,仅当开启备份后有效.
     */
    private void backup() {
        if (this.saver!=null) {
            this.saver.backup(this.board);
        }
    }

    /**
     * restart
     */
    public void restart() {
        //clear all.
        for (int i = 0; i < this.board.length; i++) {
            for (int j = 0; j < this.board[0].length; j++) {
                this.board[i][j] = 0;
            }
        }
        //clear backup
        if (this.saver != null) {
            this.saver.clear();
        }
    }

    /**
     * 悔棋，回到上一个状态.
     */
    public void redo() {

        if (this.saver != null) {
            //get last board
            byte[][] back = this.saver.redo();
            //if not empty
            if (back != null) {
                this.setBoard(back);
            }
        }
    }
    
    /**
     * 
     * @param save_dir 
     */
    public void saveBackup(String save_dir){
        if(this.saver!=null){
            //save
            this.saver.save(save_dir);
        }
    }
    
    /**
     * 
     * @param load_dir 
     */
    public void loadBackup(String load_dir){
        if(this.saver!=null){
            //load path
            this.saver.load(load_dir);
            //update
            byte[][] history_board=this.saver.getHistoryBoard(-1);
            this.setBoard(history_board);
        }
    }

    /**
     * set backup
     *
     * @param backup
     */
    public void setBackup(boolean backup) {
        //开启备份
        if (backup == true) {
            //如果备份之前未开启，则新建备份列表
            if (this.saver == null) {
                this.saver = new RefereeSaver();
            }
        } else {
            //关闭备份
            this.saver.clear();
            this.saver = null;
        }
    }

    /**
     * 复制当前棋盘并且返回，并不会把自身引用进行返回。
     *
     * @return the board
     */
    public byte[][] getBoard() {
        return Referee.copyMatrix(this.board);
    }

   

    /**
     * 走棋，返回棋盘上最大连接数量，如果等于五则胜利，如果等于-1，非法走棋。
     *
     * @param board
     * @param point
     * @param player_id
     * @return
     */
    public static byte[][] turn(byte[][] board, Point point, byte player_id) {
        byte[][] next = Referee.copyMatrix(board);
        next[point.x][point.y] = player_id;
        return next;
    }
    
    /**
     * 判断棋盘是否为空
     * @param board
     * @return 
     */
    public static boolean isBoardEmpty(byte[][] board){
        for(int i=0;i<board.length;i++){
            for(int j=0;j<board[0].length;j++){
                if(board[i][j]!=0){
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * 判断棋盘是否已满
     * @param board
     * @return 
     */
    public static boolean isBoardFull(byte[][] board){
        for(int i=0;i<board.length;i++){
            for(int j=0;j<board[0].length;j++){
                if(board[i][j]==0){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     *
     * @param board
     */
    public static void print(byte[][] board) {
        System.out.println("board=[");
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                System.out.print(board[i][j] + ", ");
            }
            System.out.println();
        }
        System.out.println("]");
    }

    /**
     * 动态规划，寻找到二维矩阵中最长相同数字组成的数组， 包括一行，一列，或者斜对角线。
     *
     * @param matrix
     * @param num
     * @return
     */
    public static int maxSub(byte[][] matrix, byte num) {

        //get row and column
        int row = matrix.length;
        int col = matrix[0].length;
        //invoke
        int[][][] max_sub = Referee.maxSub_ex(matrix, num);

        //max number
        int max_num = 0;

        //scan
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                //judge the max arry in which orientation
                int max_current = Referee.max(max_sub[i][j]);
                //update max array num
                if (max_num < max_current) {
                    max_num = max_current;
                }
            }
        }
        return max_num;
    }

    public static int[][][] maxSub_ex(byte[][] matrix, byte num) {
        //get row and column
        int row = matrix.length;
        int col = matrix[0].length;
        //create max sub matrax, the channel 0 is the row max, 1 is col max, 2 is inclined max，3 is anti-inclined max
        //@since 2019.2.15 修复不能识别右上角至左下角斜线的问题
        //增加第四个方向
        //@author yuwei
        int[][][] max_sub = new int[row][col][4];

        //scan all the nums
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (matrix[i][j] == num) {
                    if (i == 0 && j == 0) {
                        //initial
                        max_sub[i][j][0] = 1;
                        max_sub[i][j][1] = 1;
                        max_sub[i][j][2] = 1;
                        max_sub[i][j][3] = 1;
                    } else if (i == 0 && j != 0) {
                        //first row
                        max_sub[i][j][0] = Math.max(max_sub[i][j - 1][0] + 1, 1);
                        max_sub[i][j][1] = 1;
                        max_sub[i][j][2] = 1;
                        max_sub[i][j][3] = 1;
                    } else if (i != 0 && j == 0) {
                        //first column
                        max_sub[i][j][0] = 1;
                        max_sub[i][j][1] = Math.max(max_sub[i - 1][j][1] + 1, 1);
                        max_sub[i][j][2] = 1;
                        max_sub[i][j][3] = Math.max(max_sub[i - 1][j + 1][3] + 1, 1);
                    } else {
                        //else place
                        //row
                        max_sub[i][j][0] = Math.max(max_sub[i][j - 1][0] + 1, 1);
                        //col
                        max_sub[i][j][1] = Math.max(max_sub[i - 1][j][1] + 1, 1);
                        //incline
                        //左上角斜线
                        max_sub[i][j][2] = Math.max(max_sub[i - 1][j - 1][2] + 1, 1);
                        //新增,右上角斜线
                        if (j != (col - 1)) {
                            max_sub[i][j][3] = Math.max(max_sub[i - 1][j + 1][3] + 1, 1);
                        } else {
                            max_sub[i][j][3] = 1;
                        }
                    }
                }
            }
        }
        return max_sub;
    }

    /**
     * 寻找数字中最大的一个。
     *
     * @param num
     * @return
     */
    public static int max(int... num) {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < num.length; i++) {
            if (max < num[i]) {
                max = num[i];
            }
        }
        return max;
    }

    /**
     * 寻找最大值位置
     *
     * @param num
     * @return
     */
    public static int[] argMax(int... num) {
        int max = Integer.MIN_VALUE;
        List<Integer> index_list = new ArrayList<>();
        for (int i = 0; i < num.length; i++) {
            if (max < num[i]) {
                max = num[i];
                index_list.clear();
                index_list.add(i);
            } else if (max == num[i]) {
                index_list.add(i);
            }
        }
        int[] index_array = new int[index_list.size()];
        for (int i = 0; i < index_array.length; i++) {
            index_array[i] = index_list.get(i);
        }
        return index_array;
    }

    /**
     * 复制矩阵，切记，system.arraycopy只是总地址不一样的， 每一行的地址依旧一样。复制二维以上数组不可取。
     *
     * @param src
     * @return
     */
    public static byte[][] copyMatrix(byte[][] src) {
        byte[][] dest = new byte[src.length][src[0].length];
        for (int i = 0; i < dest.length; i++) {
            System.arraycopy(src[i], 0, dest[i], 0, dest[0].length);
        }
        return dest;
    }

    /**
     * @return the white_id
     */
    public byte getWhite_id() {
        return white_id;
    }

    /**
     * @return the black_id
     */
    public byte getBlack_id() {
        return black_id;
    }

    /**
     *
     * @return
     */
    public int getBoardWidth() {
        return this.board.length;
    }

}
