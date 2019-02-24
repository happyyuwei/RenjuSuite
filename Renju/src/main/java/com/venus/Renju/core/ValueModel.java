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
public interface ValueModel {
    
    /**
     * evaluate the borad value.
     * @param board
     * @param mine_id
     * @param oppoiste_id
     * @return 
     */
    public double evaluate(byte[][] board, byte mine_id, byte oppoiste_id);
    
}
