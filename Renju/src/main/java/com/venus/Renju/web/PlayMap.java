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

    //单例区域---------------------------------------------------------------------------
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
    //--------------------------------------------------------------------------------
    /**
     * 存储每个搜索引擎上一次调用时间，若时间超过一段时间无调用，则游戏结束 该搜索引擎实例将会被清除
     *
     * @since 2019.2.26
     * @version 1.6
     */
    final private Map<Integer, Long> time_map;
    //map, the integer id is key
    final private Map<Integer, SearchEngine> map;
    //random instance
    final private Random random;
    //搜索引擎守护线程
    private EngineMonitor engine_monitor;
    //monitor,每三分钟检查一次
    final private int kill_time = 180000;

    /**
     * constructor.
     */
    public PlayMap() {
        //create synchronized map
        this.map = Collections.synchronizedMap(new HashMap<>());
        //rand
        this.random = new Random();
        //create time map
        this.time_map = Collections.synchronizedMap(new HashMap<>());
        //monitor
        this.engine_monitor = new EngineMonitor();
    }

    /**
     * create a game and add the instance to the list
     *
     * @param ai_point
     * @param level
     * @return
     */
    public int createGame(String ai_point, int level) {
        //create random id
        int id = this.random.nextInt();
        //create referee
        Referee referee = new Referee(15);
        //set backup
        referee.setBackup(true);
        //create engine
        SearchEngine engine = new SearchEngine(ai_point, level, referee, new SimpleValueModel());
        LogCat.log(PlayMap.class, "create search engine, level=" + level);
        //register
        this.map.put(id, engine);
        //reocrd time
        this.time_map.put(id, System.currentTimeMillis());
        //如果没有监控线程启动,则启动该线程
        if (this.engine_monitor.is_start == false) {
            //create a new thread
            this.engine_monitor = new EngineMonitor();
            //start
            this.engine_monitor.start();
        }
        //return
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
        //加锁，确保操作原子性
        synchronized (engine) {
            //每次调用某个实例，更新调用时间
            this.time_map.put(session_id, System.currentTimeMillis());
            //get referee instance
            Referee referee = engine.getReferee();
            //如果第一轮ai先走，则玩家先不走
            if (ai_only == false) {
                //player turn
//            int player_max_num = referee.turn(new Point(row, col), referee.getBlack_id());
                int player_max_num = referee.turn(new Point(row, col), engine.getMine_id());
                //judge win
                if (player_max_num >= 5) {
                    //if the game is over, remove the session
                    this.map.remove(session_id);
                    //remove timestamp
                    this.time_map.remove(session_id);
                    LogCat.log(PlayMap.class, "session removed, id=" + session_id);
                    //player win
                    throw new PlayerWinException();
                }
            }
            //ai turn
            Point ai_next = engine.search();
            //int ai_max_num = referee.turn(ai_next, referee.getWhite_id());
            int ai_max_num = referee.turn(ai_next, engine.getOppoiste_id());
            //judge win
            if (ai_max_num >= 5) {
                this.map.remove(session_id);
                //remove timestamp
                this.time_map.remove(session_id);
                LogCat.log(PlayMap.class, "session removed, id=" + session_id);
                throw new AiWinException(ai_next);
            }
            return ai_next;
        }
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

    /**
     * 搜索引擎监控进程，只要一段时间内没有调用引擎，将直接移除该实例.
     */
    public class EngineMonitor extends Thread {

        private volatile boolean is_start = false;

        /**
         * clean useless search engine.
         */
        public void clean() {
            List<Integer> remove_list = new ArrayList<>();
            for (Map.Entry<Integer, Long> e : time_map.entrySet()) {
                //get the period of the last time activation
                //get engine
                long period = System.currentTimeMillis() - e.getValue();
                if (period > kill_time) {
                    //add to remove list,
                    //先记下来，稍后统一删除，避免 CurrentModificationException
                    remove_list.add(e.getKey());
                }
            }
            //remove
            for (int i = 0; i < remove_list.size(); i++) {
                //remove
                map.remove(remove_list.get(i));
//                        //clear timestamp
                time_map.remove(remove_list.get(i));
            }
        }

        /**
         *
         */
        @Override
        public synchronized void start() {
            this.is_start = true;
            super.start();
        }

        @Override
        public void run() {
            LogCat.log(PlayMap.EngineMonitor.class, "start engine mointor thread");
            while (time_map.isEmpty() == false) {
                LogCat.log(PlayMap.EngineMonitor.class, "monitor, time_map=" + time_map);
                clean();
                //every 1/3 period time, monitor
                try {
                    Thread.sleep(kill_time / 3);
                } catch (InterruptedException exc) {
                    exc.printStackTrace();
                }
            }
            this.is_start = false;
            LogCat.log(PlayMap.EngineMonitor.class, "exit monitor thread, map=" + time_map);
        }
    }

}
