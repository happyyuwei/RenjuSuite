/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venus.Renju.core;
import java.util.*;
import java.awt.Point;
/**
 *
 * @author happy
 * @param <T>
 */
public class SearchNode<T> {
    
    //current node state
    final private T value;
    //current change point
    final private Point point;
    //next nodes
    final private List<SearchNode> children;
    //farther
    final private SearchNode parent;
    //current depth
    final private int depth;
    
    /**
     * 
     * @param board
     * @param point
     * @param depth
     * @param parent 
     */
    public SearchNode(T board, Point point, int depth, SearchNode parent){
        this.value=board;
        this.parent=parent;
        this.depth=depth;
        this.children=new ArrayList<>();
        this.point=point;
    }
    
    /**
     * 
     * @param child 
     */
    public void addChild(SearchNode child){
        this.children.add(child);
    }

    /**
     * @return the node
     */
    public T getValue() {
        return value;
    }

    /**
     * @return the children
     */
    public List<SearchNode> getChildren() {
        return children;
    }

    /**
     * @return the parent
     */
    public SearchNode getParent() {
        return parent;
    }

    /**
     * @return the depth
     */
    public int getDepth() {
        return depth;
    }

    /**
     * @return the point
     */
    public Point getPoint() {
        return point;
    }
    
    
    
    
}

