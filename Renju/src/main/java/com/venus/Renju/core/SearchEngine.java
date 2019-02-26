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
public class SearchEngine {

    //max depth of a search tree
    //搜索的最大深度，不包含当前状态
    final private int max_depth;
    //referee, who will tell you how to play the game
    final private Referee referee;
    //evaluate value model
    final private ValueModel value_model;
    //player id
    final private byte mine_id;
    final private byte oppoiste_id;

    /**
     * max depth
     *
     * @param me
     * @param max_depth
     * @param referee
     * @param model
     */
    public SearchEngine(String me, int max_depth, Referee referee, ValueModel model) {
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
     *
     * @return
     */
    public Point search() {
        //get board
        byte[][] board = this.getReferee().getBoard();
        //create root
        SearchNode<byte[][]> root = new SearchNode<>(board, null, 1, null);
        //get the best node
        SearchNode node = search(root, this.max_depth, true);
        //trace back
        while (node.getParent().getParent() != null) {
            node = node.getParent();
        }
        //get action
        return node.getPoint();
    }

    /**
     * search recurasion, bottom up
     *
     * @param node
     * @param depth
     * @param me
     * @return
     */
    public SearchNode search(SearchNode node, int depth, boolean me) {

        //如果还没到根节点，但是已经赢了，则直接返回
        double value = this.value_model.evaluate((byte[][]) node.getValue(), this.getMine_id(), this.getOppoiste_id());
        if (value == ValueModel.WIN_VALUE) {
            node.evaluation = value;
            return node;
        }

        //根节点，进行评估
        if (depth <= 0) {
//             //evaluate
//            double value = this.value_model.evaluate((byte[][]) node.getValue(), this.getMine_id(), this.getOppoiste_id());
            //save in the node
            node.evaluation = value;
            return node;
        }
        //获取接下来可能走法
        //who turns
        byte player_id = me ? this.getMine_id() : this.getOppoiste_id();
        //get all the states of next step
        Point[] point_array = this.nextStates((byte[][]) node.getValue());

        //己方走棋,搜索最大值
        if (me == true) {
            //find the max value
            double max_value = Double.NEGATIVE_INFINITY;
            //max node
            SearchNode max_node = null;
            //for each
            for (int i = 0; i < point_array.length; i++) {
                //create next state
                byte[][] next_state = Referee.turn((byte[][]) node.getValue(), point_array[i], player_id);
//                //create next node and search next
                SearchNode next_node = new SearchNode(next_state, point_array[i], node.getDepth() + 1, node);
                SearchNode current_max_node = this.search(next_node, depth - 1, !me);
                //compare
                if (current_max_node.evaluation > max_value) {
                    max_value = current_max_node.evaluation;
                    max_node = current_max_node;
                }
            }
            return max_node;
        } //对方走棋，搜索最小值
        else {
            //find the min value
            double min_value = Double.POSITIVE_INFINITY;
            SearchNode min_node = null;
            for (int i = 0; i < point_array.length; i++) {
                //create next state
                byte[][] next_state = Referee.turn((byte[][]) node.getValue(), point_array[i], player_id);
//                //create next node and search next
                SearchNode next_node = new SearchNode(next_state, point_array[i], node.getDepth() + 1, node);
                SearchNode current_min_node = this.search(next_node, depth - 1, !me);
                //compare
                if (current_min_node.evaluation < min_value) {
                    min_value = current_min_node.evaluation;
                    min_node = current_min_node;
                }
            }
            return min_node;
        }
    }

//    /**
//     * return the next point.
//     *
//     * @return
//     */
//    public Point search() {
//        //initial max value
//        this.current_max_value = Double.NEGATIVE_INFINITY;
//        this.current_max_node = null;
//        //get board
//        byte[][] board = this.getReferee().getBoard();
//        //create node root
//        SearchNode<byte[][]> node = new SearchNode<>(board, null, 1, null);
//        //search
//        search(node, true);
//        //trace back
//        while (current_max_node.getParent().getParent() != null) {
//            current_max_node = current_max_node.getParent();
//        }
//        //get action
//        return current_max_node.getPoint();
//    }
//
//    /**
//     *
//     * @param node
//     * @param me
//     */
//    public void search(SearchNode node, boolean me) {
//        //if max node depth
//        if (node.getDepth() == this.max_depth) {
//            //evaluate
//            double value = this.value_model.evaluate((byte[][]) node.getValue(), this.getMine_id(), this.getOppoiste_id());
//            //update max value
//            if (value > this.current_max_value) {
//                this.current_max_value = value;
//                this.current_max_node = node;
//            }
//        } else {
//            //search next layer
//            //who turns
//            byte player_id = me ? this.getMine_id() : this.getOppoiste_id();
//            //get all the states of next step
//            Point[] point_array = this.nextStates((byte[][]) node.getValue());
////             System.out.println(Arrays.toString(point_array));
//            //for each
//            for (int i = 0; i < point_array.length; i++) {
//                //turn
//                byte[][] next_state = Referee.turn((byte[][]) node.getValue(), point_array[i], player_id);
//                //create next node and search next
//                SearchNode next_node = new SearchNode(next_state, point_array[i], node.getDepth() + 1, node);
//                search(next_node, !me);
//            }
//        }
//    }
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
