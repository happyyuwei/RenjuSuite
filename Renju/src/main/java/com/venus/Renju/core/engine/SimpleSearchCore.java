/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venus.Renju.core.engine;

import com.venus.Renju.core.Referee;
import com.venus.Renju.core.SearchNode;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author happy
 */
public class SimpleSearchCore {
    
     //search state
    //max value
    private double current_max_value;
    //node of max value
    private SearchNode current_max_node;
    
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

}
