/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venus.Renju.core.engine;

import com.venus.Renju.core.Referee;
import com.venus.Renju.core.ValueModel;

import java.awt.*;

/**
 * SimpleSearchCore 的子类，仅仅用于更加简单的新手入门
 * 有一定概率随便走
 * @author happy
 */
public class RandomSimpleSearchCore extends SimpleSearchCore{
    
    //有一定概率乱走
   private double prob=0;
    
    /**
     * 
     * @param me
     * @param referee
     * @param model 
     * @param prop 
     */
    public RandomSimpleSearchCore(String me, Referee referee, ValueModel model, double prop){
        super(me,referee,model);
        this.prob=prop;
    }
    
    /**
     * 
     * @param prob 
     */
    public void setProbablility(double prob){
        this.prob=prob;
    }
    
    @Override
    public Point search(){
        if(Math.random()>this.prob){
            return super.search();
        }else{
            //随机走
            Point[] points=super.nextStates(super.getReferee().getBoard());
            int index=(int)(Math.random()*points.length);
            return points[index];
        }
    }    
}
