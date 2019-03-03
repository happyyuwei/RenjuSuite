/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venus.Renju.core;

/**
 *
 * @author happy
 */
public class SimpleValueModel implements ValueModel {

    //the value of s2,d2, s3, d3, s4,d4, and win, s is single, d is double
    final private double[] value_list = {2, 4, 64, 256, 1024,4096*2 , ValueModel.WIN_VALUE};

    /**
     *
     * @param board
     * @param mine_id
     * @param opposite_id
     * @return
     */
    @Override
    public double evaluate(byte[][] board, byte mine_id, byte opposite_id) {

        //mine value
        double mine_value = this.evaluate_oneside(board, mine_id);
        //opposite value
        double opposite_value = this.evaluate_oneside(board, opposite_id);
        return mine_value - opposite_value;
    }

    /**
     * 评估某一边棋局频数，返回频数列表。 频数列表为： 单边活二，双边活二，单边活三，双边活三，单边活四，双边活四，活五的数量。
     *
     * @param board
     * @param player_id
     * @return
     */
    public int[] envaluate_frequency(byte[][] board, byte player_id) {

        //frequency array        
        int[] freq_array = new int[value_list.length];
        //max sub matrix
        int[][][] max_sub = Referee.maxSub_ex(board, player_id);
        //scan
        for (int i = 0; i < max_sub.length; i++) {
            for (int j = 0; j < max_sub[0].length; j++) {
                //max sub len of current location
                int sub_len = Referee.max(max_sub[i][j]);
                //
                if (sub_len >= 2) {
                    //find location
                    //如果两个方向数量一样，均需要比较
                    int[] orient = Referee.argMax(max_sub[i][j]);
                    for (int k = 0; k < orient.length; k++) {
                        //initial free side number
                        int free_side = 0;
                        //row orient
                        //水平方向
                        if (orient[k] == 0) {
                            //如果是是更多连子的子串，不计入总数
                            if (j >= board.length - 1 || board[i][j + 1] != player_id) {
                                //右边没有超过边界，且右边是否有阻拦
                                if (j < (board[0].length - 1) && board[i][j + 1] == 0) {
                                    free_side = free_side + 1;
                                }
                                //左边没有超过边界， 且左边是否有阻拦
                                if ((j - sub_len) >= 0 && board[i][j - sub_len] == 0) {
                                    free_side = free_side + 1;
                                }
                            }
                        } //垂直方向
                        else if (orient[k] == 1) {
                            //如果是是更多连子的子串，不计入总数
                            if (i >= board.length - 1 || board[i + 1][j] != player_id) {
                                //上面是否超过边界且上边是否阻拦
                                if (i < (board.length - 1) && board[i + 1][j] == 0) {
                                    free_side = free_side + 1;
                                }
                                //下边是否超过边界，且下面是否有阻拦
                                if ((i - sub_len) >= 0 && board[i - sub_len][j] == 0) {
                                    free_side = free_side + 1;
                                }
                            }
                        } //左上将斜线方向
                        else if (orient[k] == 2) {
                            //如果是是更多连子的子串，不计入总数
                            if (i >= board.length - 1 || j >= board[0].length-1 || board[i + 1][j + 1] != player_id) {
                                //右下角是否超过边界，且右下角是否有阻拦
                                if (i < (board.length - 1) && j < (board[0].length - 1) && board[i + 1][j + 1] == 0) {
                                    free_side = free_side + 1;
                                }
                                //左上角是否超过边界，且左上角是否有阻拦
                                if ((i - sub_len) >= 0 && (j - sub_len) >= 0 && board[i - sub_len][j - sub_len] == 0) {
                                    free_side = free_side + 1;
                                }
                            }
                        } //新增，右上角斜线方向
                        else if (orient[k] == 3) {
                            if (i >= board.length - 1 || j < 1 || board[i + 1][j - 1] != player_id) {
                                //左下角是否超过边界，且左下角是否有阻拦
                                if (i < (board.length - 1) && j > 0 && board[i + 1][j - 1] == 0) {
                                    free_side = free_side + 1;
                                }
                                //右上角是否超过边界，且右上角是否有阻拦
                                if ((i - sub_len) >= 0 && (j + sub_len) < (board.length ) && board[i - sub_len][j + sub_len] == 0) {
                                    free_side = free_side + 1;
                                }
                            }
                        }
                        //if not all the sides are brocked, save if
                        if (free_side > 0 && sub_len < 5) {
                            int index = (sub_len - 2) * 2 + (free_side - 1);
                            freq_array[index] = freq_array[index] + 1;
                        } else if (sub_len >= 5) {
                            freq_array[freq_array.length - 1] = freq_array[freq_array.length - 1] + 1;
                        }
                    }
                }
            }
        }
        return freq_array;
    }

    /**
     * 评估棋局某一边的价值。
     *
     * @param board
     * @param player_id
     * @return
     */
    public double evaluate_oneside(byte[][] board, byte player_id) {

        //frequency array        
        int[] freq_array = this.envaluate_frequency(board, player_id);
        //calculate value
        double value = 0;
        for (int i = 0; i < freq_array.length; i++) {
            value = value + this.value_list[i] * freq_array[i];
        }
        return value;

    }

}
