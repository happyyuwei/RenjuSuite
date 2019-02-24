/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venus.Renju.web;

import java.util.*;
import java.awt.Point;
import com.venus.Renju.core.*;

/**
 *
 * @author happy
 */
public class PlayMap {

    //single instance
    final private static PlayMap play_map = new PlayMap();

    /**
     * get instance
     *
     * @return
     */
    public static PlayMap getInstance() {
        return play_map;
    }

    //map, the integer id is key
    final private Map<Integer, SearchEngine> map;
    //random instance
    final private Random random;

    /**
     * constructor.
     */
    public PlayMap() {
        //create synchronized map
        this.map = Collections.synchronizedMap(new HashMap<>());
        //rand
        this.random = new Random();
    }

    /**
     * create a game and add the instance to the list
     *
     * @param ai_point
     * @return
     */
    public int createGame(String ai_point) {
        //create random id
        int id = this.random.nextInt();
        //create referee
        Referee referee = new Referee(15);
        //set backup
        referee.setBackup(true);
        //create engine
//        SearchEngine engine = new SearchEngine(ai_point,2, referee, new SimpleValueModel());
        SearchEngine engine = new SearchEngine(Referee.BLACK_PLAYER, 2, referee, new SimpleValueModel());
        //register
        this.map.put(id, engine);
        return id;
    }

    /**
     *
     * @param session_id
     * @param ai_only
     * @param row
     * @param col
     * @return
     * @throws com.venus.Renju.web.PlayMap.PlayerWinException
     * @throws com.venus.Renju.web.PlayMap.AiWinException
     */
    public Point turn(int session_id, boolean ai_only, int row, int col) throws PlayerWinException, AiWinException {

        //get current engine by id
        SearchEngine engine = this.map.get(session_id);
        //if no such id, return null
        if (engine == null) {
            return null;
        }
        //get referee instance
        Referee referee = engine.getReferee();
        //如果第一轮ai先走，则玩家先不走
        if (ai_only == false) {
            //player turn
            int player_max_num = referee.turn(new Point(row, col), referee.getBlack_id());
            //judge win
            if (player_max_num >= 5) {
                this.map.remove(session_id);
                //player win
                throw new PlayerWinException();
            }
        }
        //ai turn
        Point ai_next = engine.search();
        int ai_max_num = referee.turn(ai_next, referee.getWhite_id());
        //judge win
        if (ai_max_num >= 5) {
            this.map.remove(session_id);
            throw new AiWinException(ai_next);
        }
        return ai_next;
    }

    /**
     * player win.
     */
    public static class PlayerWinException extends Exception {
        //nothing necessary
    }

    /**
     * ai win.
     */
    public static class AiWinException extends Exception {

        //ai win point
        final private Point win_point;

        /**
         * constructor
         *
         * @param win_point
         */
        public AiWinException(Point win_point) {
            this.win_point = win_point;
        }

        /**
         * @return the win_point
         */
        public Point getWin_point() {
            return win_point;
        }

    }

}
