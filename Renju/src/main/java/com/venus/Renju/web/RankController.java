/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venus.Renju.web;

import com.venus.Renju.StreamLoader;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 *
 * @author happy
 */
@CrossOrigin(origins = "*")
@RestController
public class RankController {
    
    //user name
    final public static String KEY_USER="user";
    //score
    final public static String KEY_SCORE="score";
    

    /**
     * 
     * @return 
     */
     @RequestMapping(value = "/renju/rank")
    public List<RankController.UserScore> rank() {
        //log
        LogCat.log(RankController.class, "request rank list.");
        //read rank file
        Map<String,String> map=StreamLoader.readMap(".\\data\\rank.txt", ",");
        //create list
        List<RankController.UserScore> rank_list=new ArrayList<>();
        //to a list
        for(Map.Entry<String,String> e:map.entrySet()){
            //create score record
            RankController.UserScore score=new RankController.UserScore();
            score.setName(e.getKey());
            score.setScore(Integer.parseInt(e.getValue()));
            //add to list
            rank_list.add(score);
        }
        //sort
        return rank_list;
    }
    
    public static class UserScore{
        private String name;
        private int score;

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the score
         */
        public int getScore() {
            return score;
        }

        /**
         * @param score the score to set
         */
        public void setScore(int score) {
            this.score = score;
        }
    }
}
