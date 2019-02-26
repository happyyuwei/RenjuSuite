/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venus.Renju.core.engine;

import com.venus.Renju.core.Referee;
import com.venus.Renju.core.ValueModel;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * 搜索内核基类，实现不同搜索成都，实现难易程度，均会被SearchEngine类调用
 *
 * @since 2019.2.26
 * @version 1.7
 * @author happy
 */
public abstract class SearchCore {

    //max depth of a search tree
    //搜索的最大深度，不包含当前状态
    private final int max_depth;
    //referee, who will tell you how to play the game
    private final Referee referee;
    //evaluate value model
    private final ValueModel value_model;
    //player id
    private final byte mine_id;
    private final byte oppoiste_id;

    /**
     *
     * @param me
     * @param max_depth
     * @param referee
     * @param model
     */
    public SearchCore(String me, int max_depth, Referee referee, ValueModel model) {
        //initial object
        this.max_depth = max_depth;
        this.referee = referee;
        this.value_model = model;
        //my player
        if (me.equals(Referee.BLACK_PLAYER)) {
            this.mine_id = referee.getBlack_id();
            this.oppoiste_id = referee.getWhite_id();
        } else {
            //oppoiste
            this.mine_id = referee.getWhite_id();
            this.oppoiste_id = referee.getBlack_id();
        }
    }

    /**
     * SearchEngine 类会调用该方法，由子类实现.
     *
     * @return
     */
    public abstract Point search();

    /**
     * 枚举所有接下来的可能走棋状态。
     *
     * @param board
     * @return
     */
    public Point[] nextStates(byte[][] board) {
        //create list
        List<Point> state_list = new ArrayList<>();
        //loop and find the legal states
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == 0) {
                    state_list.add(new Point(i, j));
                }
            }
        }
        //to point array
        Point[] array = new Point[state_list.size()];
        return state_list.toArray(array);
    }

    /**
     * @return the max_depth
     */
    public int getMax_depth() {
        return max_depth;
    }

    /**
     * @return the referee
     */
    public Referee getReferee() {
        return referee;
    }

    /**
     * @return the value_model
     */
    public ValueModel getValue_model() {
        return value_model;
    }

    /**
     * @return the mine_id
     */
    public byte getMine_id() {
        return mine_id;
    }

    /**
     * @return the oppoiste_id
     */
    public byte getOppoiste_id() {
        return oppoiste_id;
    }

}
