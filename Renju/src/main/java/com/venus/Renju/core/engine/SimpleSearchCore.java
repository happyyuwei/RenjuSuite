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
 * 简单搜索只考虑 N 步之后的最佳情况，不考虑博弈过程,
 * 通常情况下，该搜索只考虑下一步有意义，
 * 在当前版本中，只允许向下搜索一层.
 * @author happy
 */
public class SimpleSearchCore extends SearchCore{
    
     //search state
    //max value
    private double current_max_value;
    //node of max value
    private SearchNode current_max_node;
    
    /**
     * constructor
     * @param me
     * @param max_depth
     * @param referee
     * @param model 
     */
     public SimpleSearchCore(String me, Referee referee, ValueModel model) {
         //在该算法中，深度是指从当前节点出发的最大深度，当前节点为1，因此只搜索下一层最大深度为2
         //invoke super constructor
         super(me,2,referee,model);
     }
    
    /**
     * return the next point.
     *
     * @return
     */
     @Override
    public Point search() {
        //initial max value
        this.current_max_value = Double.NEGATIVE_INFINITY;
        this.current_max_node = null;
        //get board
        byte[][] board = super.getReferee().getBoard();
        //create node root
        SearchNode<byte[][]> node = new SearchNode<>(board, null, 1, null);
        //search
        search(node, true);
        //trace back
        while (this.current_max_node.getParent().getParent() != null) {
            this.current_max_node = this.current_max_node.getParent();
        }
        //get action
        return this.current_max_node.getPoint();
    }

    /**
     *
     * @param node
     * @param me
     */
    public void search(SearchNode node, boolean me) {
        //if max node depth
        if (node.getDepth() == super.getMax_depth()) {
            //evaluate
            double value = super.getValue_model().evaluate((byte[][]) node.getValue(), super.getMine_id(), super.getOppoiste_id());
            //update max value
            if (value > this.current_max_value) {
                this.current_max_value = value;
                this.current_max_node = node;
            }
        } else {
            //search next layer
            //who turns
            byte player_id = me ? super.getMine_id() : super.getOppoiste_id();
            //get all the states of next step
            Point[] point_array = super.nextStates((byte[][]) node.getValue());
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

}
