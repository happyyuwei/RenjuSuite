/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venus.Renju.core;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author happy
 */
public class SearchEngine1 {

    //max depth of a search tree
    final private int max_depth;
    //referee, who will tell you how to play the game
    final private Referee referee;
    //evaluate value model
    final private ValueModel value_model;
    //player id
    final private byte mine_id;
    final private byte oppoiste_id;

    //search state
    //max value
    private double current_max_value;
    //node of max value
    private SearchNode current_max_node;

    /**
     * max depth
     *
     * @param me
     * @param max_depth
     * @param referee
     * @param model
     */
    public SearchEngine1(String me, int max_depth, Referee referee, ValueModel model) {
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
     * return the next point.
     *
     * @return
     */
    public Point search() {
        //initial max value
        this.current_max_value = Double.NEGATIVE_INFINITY;
        this.current_max_node = null;
        //get board
        byte[][] board = this.getReferee().getBoard();
        //create node root
        SearchNode<byte[][]> node = new SearchNode<>(board, null, 1, null);
        //search
        search(node, true);
        //trace back
        while (current_max_node.getParent().getParent() != null) {
            current_max_node = current_max_node.getParent();
        }
        //get action
        return current_max_node.getPoint();
    }

    /**
     *
     * @param node
     * @param me
     */
    public void search(SearchNode node, boolean me) {
        //if max node depth
        if (node.getDepth() == this.max_depth) {
            //evaluate
            double value = this.value_model.evaluate((byte[][]) node.getValue(), this.getMine_id(), this.getOppoiste_id());
            //update max value
            if (value > this.current_max_value) {
                this.current_max_value = value;
                this.current_max_node = node;
            }
        } else {
            //search next layer
            //who turns
            byte player_id = me ? this.getMine_id() : this.getOppoiste_id();
            //get all the states of next step
            Point[] point_array = this.nextStates((byte[][]) node.getValue());
//             System.out.println(Arrays.toString(point_array));
            //for each
            for (int i = 0; i < point_array.length; i++) {
                //turn
                byte[][] next_state = Referee.turn((byte[][]) node.getValue(), point_array[i], player_id);
                //create next node and search next
                SearchNode next_node = new SearchNode(next_state, point_array[i], node.getDepth() + 1, node);
                search(next_node, !me);
            }
        }
    }

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
     * @return the referee
     */
    public Referee getReferee() {
        return referee;
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
