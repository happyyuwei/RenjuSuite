/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venus.Renju.core.engine;

import com.venus.Renju.core.Referee;
import com.venus.Renju.core.SearchNode;
import com.venus.Renju.core.ValueModel;
import java.awt.Point;

/**
 * 极大极小搜索,当向下搜索一层时，和SimpleSearchCore效果一致，但不如他快.
 *
 * @author happy
 */
public class MaxMinSearchCore extends SearchCore {

    /**
     * constructor
     * @param me
     * @param max_depth
     * @param referee
     * @param model 
     */
    public MaxMinSearchCore(String me, int max_depth, Referee referee, ValueModel model) {
        super(me, max_depth, referee, model);
    }
    
    /**
     *
     * @return
     */
    @Override
    public Point search() {
        
        //第一次走棋走最中间
        if(Referee.isBoardEmpty(super.getReferee().getBoard())==true){
            return new Point(7,7);
        }
        //不是先手走棋，则进行搜索
        //get board
        byte[][] board = this.getReferee().getBoard();
        //create root
        SearchNode<byte[][]> root = new SearchNode<>(board, null, 1, null);
        //get the best node
        SearchNode node = search(root, super.getMax_depth(), true);
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
        double value = super.getValue_model().evaluate((byte[][]) node.getValue(), this.getMine_id(), this.getOppoiste_id());
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

}
