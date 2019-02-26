/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venus.Renju.core;

import com.venus.Renju.core.engine.*;
import java.awt.Point;

/**
 *
 * @author happy
 */
public class SearchEngine {

    //定义难度等级
    //难度1-3为最简单难度，带有一定概率出错
    final public static int LEVEL_EASY_1=1;
    final public static int LEVEL_EASY_2=2;
    final public static int LEVEL_EASY_3=3;
    /**
     * 难度中为普通难度，能防守所有单一连珠，但很难防守多连珠，且很少有主动进攻性，
     * 该难度将调用SimpleSearchCore类.
     */
    final public static int LEVEL_MIDDLE=11;
    
    /**
     * 难度困难,包含从考虑接下来1步道N不的最优情况.
     */
    final public static int LEVEL_DIFFICULT_1=21;
    
    
    
    //search core
    final private SearchCore search_core;
    
    /**
     * max depth
     *
     * @param me
     * @param max_depth
     * @param referee
     * @param model
     */
    public SearchEngine(String me, int max_depth, Referee referee, ValueModel model) {
          this.search_core=new SimpleSearchCore(me,referee,model);
    }

    /**
     *
     * @return
     */
    public Point search() {
      return this.search_core.search();
    }

    /**
     * @return the referee
     */
    public Referee getReferee() {
        return this.search_core.getReferee();
    }

    /**
     * @return the mine_id
     */
    public byte getMine_id() {
        return this.search_core.getMine_id();
    }

    /**
     * @return the oppoiste_id
     */
    public byte getOppoiste_id() {
        return this.search_core.getOppoiste_id();
    }

}
